package com.turik2304.coursework

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.data.network.models.data.MessageData
import com.turik2304.coursework.databinding.ActivityChatBinding
import com.turik2304.coursework.databinding.BottomSheetBinding
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import com.turik2304.coursework.presentation.base.MviActivity
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.recycler_view.AsyncAdapter
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.PaginationScrollListener
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.base.Recycler
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.clicks.ChatClickMapper
import com.turik2304.coursework.presentation.recycler_view.items.BottomSheetReactionUI
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import com.turik2304.coursework.presentation.utils.Error
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

class ChatActivity : MviActivity<ChatActions, ChatUiState>() {

    companion object {
        private const val POSITION_OF_UPPER_MESSAGE_IN_PAGE = 1
        private const val UID_OF_MESSAGE_AT_FIRST_LOAD = "newest"
        const val EXTRA_NAME_OF_TOPIC = "EXTRA_NAME_OF_TOPIC"
        const val EXTRA_NAME_OF_STREAM = "EXTRA_NAME_OF_STREAM"
    }

    @Inject
    override lateinit var store: Store<ChatActions, ChatUiState>

    @Inject
    override lateinit var actions: PublishRelay<ChatActions>

    @Inject
    lateinit var wiring: CompositeDisposable

    @Inject
    lateinit var viewBinding: CompositeDisposable

    @Inject
    lateinit var diffCallback: DiffCallback<ViewTyped>

    @Inject
    lateinit var holderFactory: HolderFactory

    private lateinit var chatBinding: ActivityChatBinding
    private lateinit var dialogBinding: BottomSheetBinding

    private lateinit var dialog: BottomSheetDialog
    private lateinit var recycler: Recycler<ViewTyped>
    private lateinit var recyclerSheet: Recycler<ViewTyped>

    private lateinit var nameOfTopic: String
    private lateinit var nameOfStream: String

    private var messagesQueueId: String = ""
    private var lastMessageEventId: String = ""
    private var isLastPage = false
    private var isLoading = false
    private var uidOfUpperMessage: String = UID_OF_MESSAGE_AT_FIRST_LOAD

    private var reactionsQueueId: String = ""
    private var lastReactionEventId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)
        (application as MyApp).chatComponent?.inject(this)

        wiring += store.wire()
        viewBinding += store.bind(this)

        initNamesOfTopicAndStream()
        initBottomSheetDialog()
        initBottomSheetRecycler()
        initRecycler()
        initRecyclersClicks()
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

        renderLoading(state.isLoading)
        renderError(state.error)
        renderBottomSheetDialog(state.auxiliaryData)
        renderClickedMessage(state.messageClicked)

        when (state.data) {
            is MessageData.FirstPageData -> renderFirstPage(state.data.messages)
            is MessageData.NextPageData -> renderNextPage(state.data.messages)
            is MessageData.SentMessageData -> renderSentMessages(state.data.messages)

            is MessageData.EventRegistrationData -> initEventsRegistrationData(state.data)
            is MessageData.MessageLongpollingData -> renderPolledMessages(state.data)
            is MessageData.ReactionLongpollingData -> renderPolledReactions(state.data)
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
        viewBinding.clear()
    }

    private fun renderLoading(isLoading: Boolean) {
        if (isLoading) {
            chatBinding.chatShimmer.showShimmer(true)
        } else {
            chatBinding.chatShimmer.stopAndHideShimmer()
        }
    }

    private fun renderError(error: Throwable?) {
        error?.let { Error.showError(applicationContext, it) }
    }

    private fun renderBottomSheetDialog(auxiliaryData: List<*>?) {
        auxiliaryData?.let { data ->
            if (data.first() is BottomSheetReactionUI) {
                recyclerSheet.setItems(data as List<ViewTyped>)
            }
        }
    }

    private fun renderClickedMessage(messageClicked: Boolean) {
        if (messageClicked) {
            dialog.show()
        } else dialog.dismiss()
    }

    private fun renderFirstPage(messages: List<ViewTyped>) {
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

    private fun renderNextPage(newPage: List<ViewTyped>) {
        wiring.clear()
        wiring += store.wire()
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

    private fun renderSentMessages(sentMessages: List<ViewTyped>) {
        val currentList = recycler.adapter.items
        recycler.updateList(currentList + sentMessages) {
            chatBinding.recycleView.smoothScrollToPosition(recycler.adapter.itemCount)
        }
    }

    private fun initEventsRegistrationData(registrationData: MessageData.EventRegistrationData) {
        messagesQueueId = registrationData.messagesQueueId
        lastMessageEventId = registrationData.messageEventId
        reactionsQueueId = registrationData.reactionsQueueId
        lastReactionEventId = registrationData.reactionEventId
        sendActionToPollEvents()
    }

    private fun renderPolledMessages(messageLongpollingData: MessageData.MessageLongpollingData) {
        messagesQueueId = messageLongpollingData.messagesQueueId
        lastMessageEventId = messageLongpollingData.lastMessageEventId
        recycler.updateList(messageLongpollingData.polledData) {
            chatBinding.recycleView.smoothScrollToPosition(
                recycler.adapter.itemCount
            )
            sendActionToPollEvents()
        }
    }

    private fun renderPolledReactions(reactionLongpollingData: MessageData.ReactionLongpollingData) {
        reactionsQueueId = reactionLongpollingData.reactionsQueueId
        lastReactionEventId = reactionLongpollingData.lastReactionEventId
        recycler.updateList(reactionLongpollingData.polledData) {
            sendActionToPollEvents()
        }
    }

    private fun checkWiring() {
        if (wiring.size() == 0) {
            wiring += store.wire()
            sendActionToPollEvents()
        }
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
    }

    private fun initRecycler() {
        recycler = Recycler(
            recyclerView = chatBinding.recycleView,
            diffCallback = diffCallback,
            holderFactory = holderFactory
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

    private fun initBottomSheetRecycler() {
        recyclerSheet = Recycler<ViewTyped>(
            recyclerView = dialogBinding.bottomSheetRecyclerView,
            holderFactory = holderFactory
        )
        actions.accept(ChatActions.GetBottomSheetReactions)
    }

    private fun initRecyclersClicks() {
        val outMessageClick = recycler.clickedItem<OutMessageUI>(R.layout.item_outcoming_message)
        val inMessageClick = recycler.clickedItem<InMessageUI>(R.layout.item_incoming_message)
        val bottomSheetReactionClick =
            recyclerSheet.clickedItem<BottomSheetReactionUI>(R.layout.item_bottom_sheet_reaction)
        viewBinding += ChatClickMapper(
            outMessageClick,
            inMessageClick,
            bottomSheetReactionClick
        ).bind(actions)
    }

    private fun loadFirstPage() {
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
}

