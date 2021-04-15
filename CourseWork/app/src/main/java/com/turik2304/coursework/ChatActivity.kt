package com.turik2304.coursework

import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
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
import com.turik2304.coursework.network.ZulipAPICallHandler
import com.turik2304.coursework.network.RetroClient
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.PaginationScrollListener
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.ChatHolderFactory
import com.turik2304.coursework.room.Database
import com.turik2304.coursework.room.DatabaseClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ChatActivity : AppCompatActivity() {

    companion object {

        lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
        lateinit var nameOfTopic: String
        lateinit var nameOfStream: String

        val compositeDisposable = CompositeDisposable()
        var isLastPage = false
        var isLoading = false
        var uidOfLastLoadedMessage = "newest"

        fun updateMessages(
                shimmer: ShimmerFrameLayout,
                uidOfLastLoadedMessage: String,
                needOneMessage: Boolean = false,
                runnable: Runnable? = null
        ) {
            isLoading = true
            compositeDisposable.add(
                    ZulipAPICallHandler.getMessageUIListFromServer(nameOfTopic, nameOfStream, uidOfLastLoadedMessage, needOneMessage)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { list ->
                                        if (needOneMessage) {
                                            asyncAdapter.items.submitList(asyncAdapter.items.currentList + list) {
                                                runnable?.run()
                                            }
                                        } else {
                                            val lastUidOfMessageInPage = list[1].uid.toString()
                                            if (lastUidOfMessageInPage != uidOfLastLoadedMessage) {
                                                val updatedList = list + asyncAdapter.items.currentList
                                                asyncAdapter.items.submitList(updatedList.distinct()) {
                                                    runnable?.run()
                                                }
                                                this.uidOfLastLoadedMessage = lastUidOfMessageInPage
                                                isLoading = false
                                            } else isLastPage = true
                                        }
                                        shimmer.stopAndHideShimmer()
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

        fun updateMessage(uidOfMessage: Int, shimmer: ShimmerFrameLayout) {
            compositeDisposable.add(
                    ZulipAPICallHandler.updateMessageUIListAfterSendingMessage(nameOfTopic,
                            nameOfStream,
                            uidOfMessage.toString(),
                            asyncAdapter.items.currentList
                    )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ updatedList ->
                                asyncAdapter.items.submitList(updatedList)
                                shimmer.stopAndHideShimmer()
                            }, { onError ->
                                Error.showError(
                                        shimmer.context,
                                        onError
                                )
                                shimmer.stopAndHideShimmer()
                            })
            )
        }

        const val EXTRA_NAME_OF_TOPIC = "EXTRA_NAME_OF_TOPIC"
        const val EXTRA_NAME_OF_STREAM = "EXTRA_NAME_OF_STREAM"
    }

    private lateinit var chatListBinding: ActivityChatBinding
    private lateinit var dialogBinding: BottomSheetBinding
    private lateinit var db: Database
    private lateinit var dialog: BottomSheetDialog
    private var uidOfClickedMessage: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatListBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(chatListBinding.root)

        db = DatabaseClient.getInstance(this)!!
        Completable.fromCallable { Log.d("xxx", "${db.messageDao().getAll()}") }
                .subscribeOn(Schedulers.io())
                .subscribe()
        dialog = BottomSheetDialog(this)
        dialogBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.bottomSheet)

        nameOfStream = intent.getStringExtra(EXTRA_NAME_OF_STREAM).toString()
        nameOfTopic = intent.getStringExtra(EXTRA_NAME_OF_TOPIC).toString()
        chatListBinding.tvNameOfStream.text = "#$nameOfStream"
        chatListBinding.tvNameOfTopic.text = "Topic:  #${nameOfTopic?.toLowerCase()}"
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
        updateMessages(chatListBinding.chatShimmer, uidOfLastLoadedMessage)
        chatListBinding.recycleView.addOnScrollListener(object : PaginationScrollListener(chatListBinding.recycleView.layoutManager as LinearLayoutManager) {
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

        chatListBinding.imageViewSendMessage.setOnClickListener {
            if (chatListBinding.editTextEnterMessage.text.isNotEmpty()) {
                val message = chatListBinding.editTextEnterMessage.text.toString()
                chatListBinding.chatShimmer.showShimmer(true)
                compositeDisposable.add(
                        RetroClient.zulipApi.sendMessage(
                                nameOfStream = nameOfStream,
                                nameOfTopic = nameOfTopic,
                                message = message
                        )
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        { response ->
                                            updateMessages(chatListBinding.chatShimmer, response.uidOfSentMessage.toString(), needOneMessage = true) {
                                                chatListBinding.recycleView.smoothScrollToPosition(
                                                        asyncAdapter.itemCount
                                                )
                                            }
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
        Completable.fromCallable {
            db.messageDao().insertAll(asyncAdapter.items.currentList)
        }.subscribeOn(Schedulers.io())
                .subscribe()
        asyncAdapter.items.submitList(null)
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
            chatListBinding.chatShimmer.showShimmer(true)
            compositeDisposable.add(
                    RetroClient.zulipApi.sendReaction(uidOfClickedMessage, name, zulipEmojiCode)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    {
                                        updateMessage(uidOfClickedMessage, chatListBinding.chatShimmer)
                                    },
                                    { onError ->
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

