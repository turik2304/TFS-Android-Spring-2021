package com.turik2304.coursework

import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.turik2304.coursework.databinding.ActivityChatBinding
import com.turik2304.coursework.databinding.BottomSheetBinding
import com.turik2304.coursework.network.RetroClient
import com.turik2304.coursework.network.ZulipRepository
import com.turik2304.coursework.network.models.data.OperationEnum
import com.turik2304.coursework.network.models.data.ReactionEvent
import com.turik2304.coursework.network.utils.NarrowConstructor
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.PaginationScrollListener
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.ChatHolderFactory
import com.turik2304.coursework.recycler_view_base.items.OutMessageUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.SerialDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class ChatActivity : AppCompatActivity() {

    companion object {

        lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
        lateinit var nameOfTopic: String
        lateinit var nameOfStream: String
        lateinit var currentList: MutableList<ViewTyped>

        val compositeDisposable = CompositeDisposable()
        var isLastPage = false
        var isLoading = false
        var uidOfLastLoadedMessage = "newest"

        fun updateMessages(
            shimmer: ShimmerFrameLayout,
            uidOfLastLoadedMessage: String,
            needFirstPage: Boolean = false,
            runnable: Runnable? = null
        ) {
            isLoading = true
            compositeDisposable.add(
                ZulipRepository.getMessages(
                    nameOfTopic,
                    nameOfStream,
                    uidOfLastLoadedMessage,
                    needFirstPage
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { messages ->
                            if (messages.isNotEmpty()) {
                                val lastUidOfMessageInPage = messages[1].uid.toString()
                                if (lastUidOfMessageInPage != uidOfLastLoadedMessage) {
                                    val updatedList =
                                        if (needFirstPage) messages else messages + asyncAdapter.items.currentList
                                    asyncAdapter.updateList(updatedList.distinct(), runnable)
                                    this.uidOfLastLoadedMessage = lastUidOfMessageInPage
                                    isLoading = false
                                } else isLastPage = true
                                shimmer.stopAndHideShimmer()
                            }
                        },
                        { onError ->
                            Error.showError(
                                shimmer.context,
                                onError
                            )
                            shimmer.stopAndHideShimmer()
                        })
            )
        }

        fun AsyncAdapter<ViewTyped>.updateList(
            newList: List<ViewTyped>?,
            runnable: Runnable? = null
        ) {
            this.items.submitList(newList) {
                runnable?.run()
                currentList = this.items.currentList
            }

        }

        const val EXTRA_NAME_OF_TOPIC = "EXTRA_NAME_OF_TOPIC"
        const val EXTRA_NAME_OF_STREAM = "EXTRA_NAME_OF_STREAM"
    }

    private lateinit var chatBinding: ActivityChatBinding
    private lateinit var dialogBinding: BottomSheetBinding
    private lateinit var dialog: BottomSheetDialog

    private var messagesQueueId: String = ""
    private var lastMessageEventId: String = ""

    private var reactionsQueueId: String = ""
    private var lastReactionEventId: String = ""
    private val updateReactionsOfMessagesSubject = PublishSubject.create<List<ViewTyped>>()

    private val longpollingDisposable = CompositeDisposable()
    private var uidOfClickedMessage: Int = -1
    private val setOfRawUidsOfMessages = hashSetOf<Int>()

    private var isFirstLoading = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)

        dialog = BottomSheetDialog(this)
        dialogBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.bottomSheet)

        nameOfStream = intent.getStringExtra(EXTRA_NAME_OF_STREAM).toString()
        nameOfTopic = intent.getStringExtra(EXTRA_NAME_OF_TOPIC).toString()
        chatBinding.tvNameOfStream.text = "#$nameOfStream"
        chatBinding.tvNameOfTopic.text = "Topic:  #${nameOfTopic.toLowerCase()}"
        chatBinding.imageViewBackButton.setOnClickListener { onBackPressed() }

        fillTextViewWithEmojisAsSpannableText(
            textView = dialogBinding.emojiListTextView,
            emojiCodeRange = 0x1F600..0x1F645
        )
        val clickListener = { clickedView: View ->
            when (clickedView) {
                is MessageViewGroup -> {
                    uidOfClickedMessage = clickedView.uid
                    dialog.show()
                }
                //ImageView that adds Emoji("+"), it are located in FlexBoxLayout, and FlexBox located in MessageViewGroup
                is ImageView -> {
                    val flexBoxLayout = clickedView.parent as FlexboxLayout
                    val messageViewGroup = flexBoxLayout.parent as MessageViewGroup
                    uidOfClickedMessage = messageViewGroup.uid
                    dialog.show()
                }
            }
        }

        val holderFactory = ChatHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        chatBinding.recycleView.adapter = asyncAdapter

        updateMessages(
            chatBinding.chatShimmer,
            uidOfLastLoadedMessage,
            needFirstPage = true
        ) {
            chatBinding.recycleView.smoothScrollToPosition(asyncAdapter.itemCount)
        }

        //catches reactions events
        compositeDisposable.add(
            updateReactionsOfMessagesSubject
                .debounce(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    val actualChanges = asyncAdapter.items.currentList - result
                    asyncAdapter.updateList((result + actualChanges).distinctBy { it.uid })
                })

        chatBinding.recycleView.addOnScrollListener(object :
            PaginationScrollListener(chatBinding.recycleView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                updateMessages(chatBinding.chatShimmer, uidOfLastLoadedMessage)
            }
        })

        chatBinding.imageViewSendMessage.setOnClickListener {
            if (chatBinding.editTextEnterMessage.text.isNotEmpty()) {
                val message = chatBinding.editTextEnterMessage.text.toString()
                chatBinding.chatShimmer.showShimmer(true)
                val rawMessage = OutMessageUI(
                    nameOfStream = nameOfStream,
                    nameOfTopic = nameOfTopic,
                    userName = "",
                    userId = MyUserId.MY_USER_ID,
                    message = message,
                    reactions = emptyList(),
                    dateInSeconds = (Calendar.getInstance().timeInMillis / 1000).toInt(),
                    uid = -abs(asyncAdapter.items.currentList.last().uid) - 1
                )
                setOfRawUidsOfMessages.add(rawMessage.uid)
                val newList = asyncAdapter.items.currentList + listOf(rawMessage)
                asyncAdapter.updateList(newList) {
                    chatBinding.recycleView.smoothScrollToPosition(asyncAdapter.itemCount)
                }
                compositeDisposable.add(
                    RetroClient.zulipApi.sendMessage(
                        nameOfStream = nameOfStream,
                        nameOfTopic = nameOfTopic,
                        message = message
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                chatBinding.chatShimmer.stopAndHideShimmer()
                            },
                            { onError ->
                                Error.showError(
                                    applicationContext,
                                    onError
                                )
                                chatBinding.chatShimmer.stopAndHideShimmer()
                            })
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

    override fun onStart() {
        super.onStart()
        chatBinding.chatShimmer.startShimmer()
        val narrow = NarrowConstructor.getNarrowArray(nameOfTopic, nameOfStream)
        if (isFirstLoading) {
            longpollingDisposable.add(
                RetroClient.zulipApi.registerMessageEvents(narrow)
                    .onErrorComplete()
                    .subscribeOn(Schedulers.io())
                    .subscribe { registerMessagesEventsResponse ->
                        messagesQueueId = registerMessagesEventsResponse.queueId
                        lastMessageEventId = registerMessagesEventsResponse.lastEventId
                        initMessagesLongpolling()
                        longpollingDisposable.add(
                            RetroClient.zulipApi.registerReactionEvents(narrow)
                                .onErrorComplete()
                                .subscribe { registerReactionsEventsResponse ->
                                    reactionsQueueId = registerReactionsEventsResponse.queueId
                                    lastReactionEventId =
                                        registerReactionsEventsResponse.lastEventId
                                    initReactionsLongpolling()
                                    isFirstLoading = false
                                })
                    })
        } else {
            initMessagesLongpolling()
            initReactionsLongpolling()
        }
    }

    override fun onStop() {
        super.onStop()
        longpollingDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        RetroClient.zulipApi.unregisterEvents(messagesQueueId)
            .onErrorComplete()
            .subscribeOn(Schedulers.io())
            .subscribe {
                RetroClient.zulipApi.unregisterEvents(reactionsQueueId)
                    .onErrorComplete()
                    .subscribe()
            }
        asyncAdapter.updateList(null)
        asyncAdapter.holderFactory = null
        chatBinding.recycleView.adapter = null
        compositeDisposable.clear()
        isLastPage = false
        isLoading = false
        uidOfLastLoadedMessage = "newest"
    }

    private fun initMessagesLongpolling() {
        val serialDisposable = SerialDisposable()
        longpollingDisposable.add(serialDisposable)
        serialDisposable.set(
            Observable.interval(2, TimeUnit.SECONDS)
                .flatMap {
                    ZulipRepository.getMessageEvent(
                        messagesQueueId,
                        lastMessageEventId,
                        nameOfTopic,
                        nameOfStream,
                        asyncAdapter.items.currentList,
                        setOfRawUidsOfMessages
                    )
                }
                .retry()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { eventIdAndNewMessages ->
                    if (eventIdAndNewMessages.second.isNotEmpty()) {
                        lastMessageEventId = eventIdAndNewMessages.first
                        asyncAdapter.updateList(eventIdAndNewMessages.second) {
                            chatBinding.recycleView.smoothScrollToPosition(
                                asyncAdapter.itemCount
                            )
                        }
                    }
                })
    }

    private fun initReactionsLongpolling() {
        val serialDisposable = SerialDisposable()
        longpollingDisposable.add(serialDisposable)
        serialDisposable.set(
            Observable.interval(2, TimeUnit.SECONDS)
                .flatMap {
                    ZulipRepository.getReactionEvent(
                        reactionsQueueId,
                        lastReactionEventId,
                        currentList
                    )
                }
                .retry()
                .subscribe { eventIdToUpdatedList ->
                    if (eventIdToUpdatedList.second.isNotEmpty()) {
                        lastReactionEventId = eventIdToUpdatedList.first
                        currentList = eventIdToUpdatedList.second.toMutableList()
                        updateReactionsOfMessagesSubject.onNext(eventIdToUpdatedList.second)
                    }
                })
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
            val nameAndZulipEmojiCode = EmojiEnum.getNameByCodePoint(emojiCode)
            val name = nameAndZulipEmojiCode.first
            val zulipEmojiCode = nameAndZulipEmojiCode.second
            val reactionEvent = listOf(
                ReactionEvent(
                    id = "",
                    operation = OperationEnum.ADD,
                    emojiCode = zulipEmojiCode,
                    messageId = uidOfClickedMessage,
                    userId = MyUserId.MY_USER_ID
                )
            )
            val updatedList =
                ZulipRepository.updateReactions(asyncAdapter.items.currentList, reactionEvent)
            asyncAdapter.updateList(updatedList)
            chatBinding.chatShimmer.showShimmer(true)
            compositeDisposable.add(
                RetroClient.zulipApi.sendReaction(uidOfClickedMessage, name, zulipEmojiCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            chatBinding.chatShimmer.stopAndHideShimmer()
                        },
                        { onError ->
                            chatBinding.chatShimmer.stopAndHideShimmer()
                            Error.showError(
                                applicationContext,
                                onError
                            )
                        })
            )
            dialog.dismiss()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }
}

