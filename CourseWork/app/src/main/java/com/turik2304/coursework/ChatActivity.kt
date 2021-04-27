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
            isFirstLoad: Boolean = false,
            runnable: Runnable? = null
        ) {
            isLoading = true
            compositeDisposable.add(
                ZulipRepository.getMessages(
                    nameOfTopic,
                    nameOfStream,
                    uidOfLastLoadedMessage,
                    isFirstLoad
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { messages ->
                            if (messages.isNotEmpty()) {
                                val lastUidOfMessageInPage = messages[1].uid.toString()
                                if (lastUidOfMessageInPage != uidOfLastLoadedMessage) {
                                    val updatedList =
                                        if (isFirstLoad) messages else messages + asyncAdapter.items.currentList
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

    private lateinit var chatListBinding: ActivityChatBinding
    private lateinit var dialogBinding: BottomSheetBinding

    private lateinit var dialog: BottomSheetDialog
    private lateinit var lastMessageEventId: String
    private lateinit var reserveLastMessageEventId: String

    private var messageQueueId: String = ""
    private var reactionQueueId: String = ""
    private var uidOfClickedMessage: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatListBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatListBinding.root)

        dialog = BottomSheetDialog(this)
        dialogBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.bottomSheet)

        nameOfStream = intent.getStringExtra(EXTRA_NAME_OF_STREAM).toString()
        nameOfTopic = intent.getStringExtra(EXTRA_NAME_OF_TOPIC).toString()
        chatListBinding.tvNameOfStream.text = "#$nameOfStream"
        chatListBinding.tvNameOfTopic.text = "Topic:  #${nameOfTopic.toLowerCase()}"
        chatListBinding.imageViewBackButton.setOnClickListener { onBackPressed() }

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
        chatListBinding.recycleView.adapter = asyncAdapter
        updateMessages(
            chatListBinding.chatShimmer,
            uidOfLastLoadedMessage,
            isFirstLoad = true
        ) {
            chatListBinding.recycleView.smoothScrollToPosition(asyncAdapter.itemCount)
        }

        chatListBinding.recycleView.addOnScrollListener(object :
            PaginationScrollListener(chatListBinding.recycleView.layoutManager as LinearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                updateMessages(chatListBinding.chatShimmer, uidOfLastLoadedMessage)
            }
        })

        //messageEvents
        val setOfRawUidsOfMessages = hashSetOf<Int>()
        val narrow = NarrowConstructor.getNarrowArray(nameOfTopic, nameOfStream)
        compositeDisposable.add(
            RetroClient.zulipApi.registerMessageEvents(narrow)
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .subscribe { response ->
                    messageQueueId = response.queueId
                    lastMessageEventId = response.lastEventId
                    reserveLastMessageEventId = response.lastEventId
                    val serialDisposable = SerialDisposable()
                    compositeDisposable.add(serialDisposable)
                    serialDisposable.set(
                        Observable.interval(2, TimeUnit.SECONDS)
                            .flatMap {
                                ZulipRepository.getMessageEvent(
                                    messageQueueId,
                                    lastMessageEventId,
                                    nameOfTopic,
                                    nameOfStream,
                                    asyncAdapter.items.currentList,
                                    setOfRawUidsOfMessages
                                )
                            }
                            .retry()
                            .subscribe { eventIdAndNewMessages ->
                                if (eventIdAndNewMessages.second.isNotEmpty()) {
                                    lastMessageEventId = eventIdAndNewMessages.first
                                    asyncAdapter.updateList(eventIdAndNewMessages.second) {
                                        chatListBinding.recycleView.smoothScrollToPosition(
                                            asyncAdapter.itemCount
                                        )
                                    }
                                }
                            })
                })

        //reactionEvents
        val updatedReactionsOfMessagesSubject = PublishSubject.create<List<ViewTyped>>()
        compositeDisposable.add(
            updatedReactionsOfMessagesSubject
                .debounce(2, TimeUnit.SECONDS)
                .observeOn(Schedulers.computation())
                .subscribe { result ->
                    val actualChanges = asyncAdapter.items.currentList - result
                    asyncAdapter.updateList((result + actualChanges).distinctBy { it.uid })
                })
        compositeDisposable.add(
            RetroClient.zulipApi.registerReactionEvents(narrow)
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
                .subscribe { response ->
                    reactionQueueId = response.queueId
                    var lastEventId = response.lastEventId
                    val serialDisposable = SerialDisposable()
                    compositeDisposable.add(serialDisposable)
                    serialDisposable.set(
                        Observable.interval(2, TimeUnit.SECONDS)
                            .flatMap {
                                ZulipRepository.getReactionEvent(
                                    reactionQueueId,
                                    lastEventId,
                                    currentList
                                )
                            }
                            .retry()
                            .subscribe { eventIdToUpdatedList ->
                                if (eventIdToUpdatedList.second.isNotEmpty()) {
                                    lastEventId = eventIdToUpdatedList.first
                                    currentList = eventIdToUpdatedList.second.toMutableList()
                                    updatedReactionsOfMessagesSubject.onNext(eventIdToUpdatedList.second)
                                }
                            })
                })

        chatListBinding.imageViewSendMessage.setOnClickListener {
            if (chatListBinding.editTextEnterMessage.text.isNotEmpty()) {
                val message = chatListBinding.editTextEnterMessage.text.toString()
                chatListBinding.chatShimmer.showShimmer(true)
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
                    chatListBinding.recycleView.smoothScrollToPosition(asyncAdapter.itemCount)
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
                                chatListBinding.chatShimmer.stopAndHideShimmer()
                            },
                            { onError ->
                                Error.showError(
                                    applicationContext,
                                    onError
                                )
                                chatListBinding.chatShimmer.stopAndHideShimmer()
                            })
                )
                chatListBinding.editTextEnterMessage.text.clear()
            }
        }

        chatListBinding.editTextEnterMessage.addTextChangedListener(object : TextWatcher {
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
                    0 -> chatListBinding.imageViewSendMessage.setImageResource(R.drawable.ic_add_files)
                    1 -> chatListBinding.imageViewSendMessage.setImageResource(R.drawable.ic_send_message)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        chatListBinding.chatShimmer.startShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        RetroClient.zulipApi.unregisterEvents(messageQueueId)
            .onErrorComplete()
            .subscribeOn(Schedulers.io())
            .subscribe {
                RetroClient.zulipApi.unregisterEvents(reactionQueueId)
                    .onErrorComplete()
                    .subscribe()
            }
        asyncAdapter.updateList(null)
        asyncAdapter.holderFactory = null
        chatListBinding.recycleView.adapter = null
        compositeDisposable.clear()
        isLastPage = false
        isLoading = false
        uidOfLastLoadedMessage = "newest"
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
            chatListBinding.chatShimmer.showShimmer(true)
            compositeDisposable.add(
                RetroClient.zulipApi.sendReaction(uidOfClickedMessage, name, zulipEmojiCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            chatListBinding.chatShimmer.stopAndHideShimmer()
                        },
                        { onError ->
                            chatListBinding.chatShimmer.stopAndHideShimmer()
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

