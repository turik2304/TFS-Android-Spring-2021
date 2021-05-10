package com.turik2304.coursework

import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.data.EmojiEnum
import com.turik2304.coursework.data.network.models.data.MessageData
import com.turik2304.coursework.databinding.ActivityChatBinding
import com.turik2304.coursework.databinding.BottomSheetBinding
import com.turik2304.coursework.domain.chat_middlewares.*
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatReducer
import com.turik2304.coursework.presentation.ChatUiState
import com.turik2304.coursework.presentation.base.MviActivity
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.recycler_view.AsyncAdapter
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.PaginationScrollListener
import com.turik2304.coursework.presentation.recycler_view.base.Recycler
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.clicks.ChatClickMapper
import com.turik2304.coursework.presentation.recycler_view.holder_factories.ChatHolderFactory
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import com.turik2304.coursework.presentation.utils.Error
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*

class ChatActivity : MviActivity<ChatActions, ChatUiState>() {

    companion object {
        private const val POSITION_OF_UPPER_MESSAGE_IN_PAGE = 1
        private const val UID_OF_MESSAGE_AT_FIRST_LOAD = "newest"
        const val EXTRA_NAME_OF_TOPIC = "EXTRA_NAME_OF_TOPIC"
        const val EXTRA_NAME_OF_STREAM = "EXTRA_NAME_OF_STREAM"
        var activityActions: PublishRelay<ChatActions>? = null
    }

    private lateinit var chatBinding: ActivityChatBinding
    private lateinit var dialogBinding: BottomSheetBinding
    private lateinit var dialog: BottomSheetDialog

    private lateinit var recycler: Recycler<ViewTyped>
    private lateinit var nameOfTopic: String
    private lateinit var nameOfStream: String

    private val wiring = CompositeDisposable()
    private val viewBinding = CompositeDisposable()

    override val store: Store<ChatActions, ChatUiState> = Store(
        reducer = ChatReducer(),
        middlewares = listOf(
            LoadMessagesMiddleware(),
            RegisterEventsMiddleware(),
            EventsLongpollingMiddleware(),
            SendMessageMiddleware(),
            AddReactionMiddleware(),
            RemoveReactionMiddleware()
        ),
        initialState = ChatUiState()
    )
    override val actions: PublishRelay<ChatActions> = PublishRelay.create()

    private var messagesQueueId: String = ""
    private var lastMessageEventId: String = ""
    private var isLastPage = false
    private var isLoading = false
    private var uidOfUpperMessage: String = UID_OF_MESSAGE_AT_FIRST_LOAD
    private var uidOfClickedMessage: Int = -1

    private var reactionsQueueId: String = ""
    private var lastReactionEventId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)

        initBottomSheetDialog()
        initNamesOfTopicAndStream()
        initRecycler()
        initRecyclerClicks()

        wiring += store.wire()
        viewBinding += store.bind(this)

        loadFirstPage()

        chatBinding.imageViewBackButton.setOnClickListener { onBackPressed() }
        chatBinding.imageViewSendMessage.setOnClickListener {
            if (chatBinding.editTextEnterMessage.text.isNotEmpty()) {
                val message = chatBinding.editTextEnterMessage.text.toString()
                actions.accept(
                    ChatActions.SendMessage(
                        nameOfTopic = nameOfTopic,
                        nameOfStream = nameOfStream,
                        message = message
                    )
                )
                chatBinding.editTextEnterMessage.text.clear()
            }
        }
        chatBinding.editTextEnterMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) {
            }

            override fun onTextChanged(
                message: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                when (message.length) {
                    0 -> chatBinding.imageViewSendMessage.setImageResource(R.drawable.ic_add_files)
                    1 -> chatBinding.imageViewSendMessage.setImageResource(R.drawable.ic_send_message)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun render(state: ChatUiState) {
        isLoading = state.isLoading
        if (state.isLoading) {
            chatBinding.chatShimmer.showShimmer(true)
        } else {
            chatBinding.chatShimmer.stopAndHideShimmer()
        }

        state.uidOfClickedMessage?.let {
            dialog.show()
            uidOfClickedMessage = state.uidOfClickedMessage
        }

        state.error?.let { Error.showError(applicationContext, it) }

        when (state.data) {
            is MessageData.FirstPageData -> {
                val messages = state.data.items
                uidOfUpperMessage =
                    messages[POSITION_OF_UPPER_MESSAGE_IN_PAGE].uid.toString()
                recycler.updateList(messages) {
                    chatBinding.recycleView.smoothScrollToPosition(recycler.adapter.itemCount)
                    actions.accept(
                        ChatActions.RegisterEvents(
                            nameOfTopic = nameOfTopic,
                            nameOfStream = nameOfStream
                        )
                    )
                }
            }
            is MessageData.SentMessageData -> {
                val currentList = recycler.adapter.items
                recycler.updateList(currentList + state.data.messages) {
                    chatBinding.recycleView.smoothScrollToPosition(recycler.adapter.itemCount)
                }
            }
            is MessageData.NextPageData -> {
                wiring.clear()
                wiring += store.wire()
                val newPage = state.data.items
                val uidOfUpperMessageInNewPage =
                    newPage[POSITION_OF_UPPER_MESSAGE_IN_PAGE].uid.toString()
                if (uidOfUpperMessageInNewPage == uidOfUpperMessage) isLastPage = true
                else {
                    val actualList = (newPage + recycler.adapter.items).distinct()
                    recycler.updateList(actualList) {
                        uidOfUpperMessage =
                            actualList[POSITION_OF_UPPER_MESSAGE_IN_PAGE].uid.toString()
                        sendActionToPollEvents()
                    }
                }
            }
            is MessageData.EventRegistrationData -> {
                messagesQueueId = state.data.messagesQueueId
                lastMessageEventId = state.data.messageEventId
                reactionsQueueId = state.data.reactionsQueueId
                lastReactionEventId = state.data.reactionEventId
                sendActionToPollEvents()
            }
            is MessageData.MessageLongpollingData -> {
                messagesQueueId = state.data.messagesQueueId
                lastMessageEventId = state.data.lastMessageEventId
                recycler.updateList(state.data.polledData) {
                    chatBinding.recycleView.smoothScrollToPosition(
                        recycler.adapter.itemCount
                    )
                    sendActionToPollEvents()
                }
            }
            is MessageData.ReactionLongpollingData -> {
                reactionsQueueId = state.data.reactionsQueueId
                lastReactionEventId = state.data.lastReactionEventId
                recycler.updateList(state.data.polledData) {
                    sendActionToPollEvents()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkWiring()
    }

    override fun onStop() {
        super.onStop()
        wiring.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityActions = null
        viewBinding.clear()
    }

    private fun checkWiring() {
        if (wiring.size() == 0) {
            wiring += store.wire()
            sendActionToPollEvents()
        }
    }

    private fun initRecycler() {
        recycler = Recycler(
            recyclerView = chatBinding.recycleView,
            diffCallback = DiffCallback<ViewTyped>(),
            holderFactory = ChatHolderFactory()
        )
        chatBinding.recycleView.addOnScrollListener(object :
            PaginationScrollListener(chatBinding.recycleView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                actions.accept(
                    ChatActions.LoadItems(
                        nameOfTopic = nameOfTopic,
                        nameOfStream = nameOfStream,
                        uidOfLastLoadedMessage = uidOfUpperMessage
                    )
                )
            }
        })
    }

    private fun initRecyclerClicks() {
        val outMessageClick = recycler.clickedItem<OutMessageUI>(R.layout.item_outcoming_message)
        val inMessageClick = recycler.clickedItem<InMessageUI>(R.layout.item_incoming_message)
        viewBinding += ChatClickMapper(outMessageClick, inMessageClick).bind(actions)
    }

    private fun initNamesOfTopicAndStream() {
        nameOfStream = intent.getStringExtra(EXTRA_NAME_OF_STREAM).toString()
        nameOfTopic = intent.getStringExtra(EXTRA_NAME_OF_TOPIC).toString()
        chatBinding.tvNameOfStream.text = getString(R.string.name_of_stream, nameOfStream)
        chatBinding.tvNameOfTopic.text = getString(
            R.string.name_of_topic,
            nameOfTopic.toLowerCase(Locale.ROOT)
        )
    }

    private fun initBottomSheetDialog() {
        dialog = BottomSheetDialog(this)
        dialogBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.bottomSheet)
        dialog.setOnDismissListener {
            actions.accept(
                ChatActions.DismissBottomSheetDialog
            )
        }
        fillTextViewWithEmojisAsSpannableText(
            textView = dialogBinding.emojiListTextView,
            emojiCodeRange = 0x1F600..0x1F645
        )
    }

    private fun loadFirstPage() {
        activityActions = actions
        actions.accept(
            ChatActions.LoadItems(
                needFirstPage = true,
                nameOfTopic = nameOfTopic,
                nameOfStream = nameOfStream,
                uidOfLastLoadedMessage = uidOfUpperMessage
            )
        )
    }

    private fun sendActionToPollEvents() {
        actions.accept(
            ChatActions.GetEvents(
                nameOfTopic = nameOfTopic,
                nameOfStream = nameOfStream,
                messageQueueId = messagesQueueId,
                messageEventId = lastMessageEventId,
                reactionQueueId = reactionsQueueId,
                reactionEventId = lastReactionEventId,
                currentList = recycler.adapter.items,
            )
        )
    }

    private fun Recycler<ViewTyped>.updateList(
        newList: List<ViewTyped>?,
        runnable: Runnable? = null
    ) {
        val adapter = this.adapter as AsyncAdapter
        if (!newList.isNullOrEmpty()) {
            adapter.setItemsWithCommitCallback(newList) {
                runnable?.run()
            }
        } else runnable?.run()
    }

    private fun fillTextViewWithEmojisAsSpannableText(
        textView: TextView,
        emojiCodeRange: IntRange
    ) {
        val stringBuilder = SpannableStringBuilder()
        for (emojiCode in emojiCodeRange) {
            val emojiString = String(Character.toChars(emojiCode))
            val prevInd = stringBuilder.length
            stringBuilder.append(SpannableString(emojiString))
            val curInd = stringBuilder.length
            stringBuilder.setSpan(
                MyClickableSpan(prevInd, curInd),
                prevInd,
                curInd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = stringBuilder.toSpannable()
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    inner class MyClickableSpan(
        private val start: Int,
        private val end: Int
    ) :
        ClickableSpan() {
        override fun onClick(widget: View) {
            val emojiCodeString = (widget as TextView).text.subSequence(start, end).toString()
            val emojiCode = emojiCodeString.codePointAt(0)
            val nameAndZulipEmojiCode = EmojiEnum.getNameAndCodeByCodePoint(emojiCode)
            val zulipEmojiName = nameAndZulipEmojiCode.first
            val zulipEmojiCode = nameAndZulipEmojiCode.second
            actions.accept(
                ChatActions.AddReaction(
                    messageId = uidOfClickedMessage,
                    emojiName = zulipEmojiName,
                    emojiCode = zulipEmojiCode
                )
            )
            dialog.dismiss()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }
}

