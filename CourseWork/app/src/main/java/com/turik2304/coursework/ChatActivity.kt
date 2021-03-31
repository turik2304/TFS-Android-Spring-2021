package com.turik2304.coursework

import android.content.Context
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.turik2304.coursework.databinding.ActivityChatBinding
import com.turik2304.coursework.databinding.BottomSheetBinding
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.ChatHolderFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class ChatActivity : AppCompatActivity() {

    companion object {
        lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
        lateinit var nameOfTopic: String
        lateinit var nameOfStream: String

        val api: ServerApi = FakeServerApi()
        fun updateMessages(
            context: Context,
            shimmer: ShimmerFrameLayout
        ) {
            api.getMessageUIListFromServer(nameOfTopic, nameOfStream)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        asyncAdapter.items.submitList(list)
                        shimmer.stopAndHideShimmer()
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        shimmer.stopAndHideShimmer()
                    })
        }

        const val EXTRA_NAME_OF_TOPIC = "EXTRA_NAME_OF_TOPIC"
        const val EXTRA_NAME_OF_STREAM = "EXTRA_NAME_OF_STREAM"
    }

    private lateinit var chatListBinding: ActivityChatBinding
    private lateinit var dialogBinding: BottomSheetBinding


    private lateinit var dialog: BottomSheetDialog
    private var uidOfClickedMessage: String = ""


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
        updateMessages(applicationContext, chatListBinding.chatShimmer)

        chatListBinding.imageViewSendMessage.setOnClickListener {
            if (chatListBinding.editTextEnterMessage.text.isNotEmpty()) {
                val message = chatListBinding.editTextEnterMessage.text.toString()
                chatListBinding.chatShimmer.showShimmer(true)
                api.sendMessageToServer(nameOfTopic, nameOfStream, message)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            api.getMessageUIListFromServer(nameOfTopic, nameOfStream)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                    { list ->
                                        asyncAdapter.items.submitList(list) {
                                            chatListBinding.recycleView.smoothScrollToPosition(
                                                asyncAdapter.itemCount
                                            )
                                            chatListBinding.chatShimmer.stopAndHideShimmer()
                                        }
                                    },
                                    { onError ->
                                        Error.showError(
                                            applicationContext,
                                            onError
                                        )
                                        chatListBinding.chatShimmer.stopAndHideShimmer()
                                    })
                        },
                        { onError ->
                            Error.showError(
                                applicationContext,
                                onError
                            )
                            chatListBinding.chatShimmer.stopAndHideShimmer()
                        })
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

    inner class MyClickableSpan(private val start: Int, private val end: Int) :
        ClickableSpan() {

        override fun onClick(widget: View) {
            val emojiCodeString = (widget as TextView).text.subSequence(start, end).toString()
            val emojiCode = emojiCodeString.codePointAt(0)
            val nameAndZulipEmojiCode = EmojiEnum.getNameByCodePoint(emojiCode)
            val name = nameAndZulipEmojiCode.first
            val zulipEmojiCode = nameAndZulipEmojiCode.second
            chatListBinding.chatShimmer.showShimmer(true)
            api.sendReaction(uidOfClickedMessage, zulipEmojiCode, name)
                .subscribe(
                    {
                        updateMessages(applicationContext, chatListBinding.chatShimmer)
                    },
                    { onError ->
                        Error.showError(
                            applicationContext,
                            onError
                        )
                    })
            dialog.dismiss()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }
}

