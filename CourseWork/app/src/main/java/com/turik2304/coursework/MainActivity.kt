package com.turik2304.coursework

import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpannable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.turik2304.coursework.databinding.BottomSheetBinding
import com.turik2304.coursework.databinding.ChatListBinding
import com.turik2304.coursework.recyclerViewBase.AsyncAdapter
import com.turik2304.coursework.recyclerViewBase.ViewTyped
import com.turik2304.coursework.recyclerViewBase.diffUtils.DiffCallbackMessageUI
import com.turik2304.coursework.recyclerViewBase.items.DateSeparatorUI
import com.turik2304.coursework.recyclerViewBase.items.InMessageUI
import com.turik2304.coursework.recyclerViewBase.items.OutMessageUI
import java.text.SimpleDateFormat
import java.util.*

const val MY_USER_ID = "ARTUR"

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var viewTypedList: MutableList<ViewTyped>
        lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
        private val fakeServer = FakeServerApi()
        fun updateReactionsOfMessages(
            dateOfMessageInMillis: Long,
            handler: (MutableList<FakeServerApi.Reaction>) -> Boolean
        ) {
            viewTypedList.forEach { item ->
                when (item.viewType) {
                    R.layout.item_incoming_message -> {
                        item as InMessageUI
                        if (item.dateInMillis == dateOfMessageInMillis) {
                            val reactions = item.reactions.toMutableList()
                            handler(reactions)
                            item.reactions = reactions
                        }
                    }
                    R.layout.item_outcoming_message -> {
                        item as OutMessageUI
                        if (item.dateInMillis == dateOfMessageInMillis) {
                            val reactions = item.reactions.toMutableList()
                            handler(reactions)
                            item.reactions = reactions
                        }
                    }
                }
            }
            //!!!
            Log.d("POPA", "before listServer ${fakeServer.messages}")
            sendMessagesToFakeServer(viewTypedList)
            Log.d("POPA", "after listServer ${fakeServer.messages}")
            val refreshedList = getMessageItemsFromFakeServer()
            asyncAdapter.items.submitList(refreshedList) {

            }
        }

        private fun sendMessagesToFakeServer(viewTypedList: List<ViewTyped>) {
            val messages = mutableListOf<FakeServerApi.Message>()
            viewTypedList.forEach { item ->
                when (item.viewType) {
                    R.layout.item_incoming_message -> {
                        item as InMessageUI
                        messages.add(
                            FakeServerApi.Message(
                                item.message,
                                item.dateInMillis,
                                item.uid,
                                item.reactions
                            )
                        )
                    }
                    R.layout.item_outcoming_message -> {
                        item as OutMessageUI
                        messages.add(
                            FakeServerApi.Message(
                                item.message,
                                item.dateInMillis,
                                item.uid,
                                item.reactions
                            )
                        )
                    }
                }
            }
            fakeServer.messages = messages
        }

        private fun getMessageItemsFromFakeServer(): List<ViewTyped> {
            val messagesByDate = fakeServer.messages
                .sortedBy { it.dateInMillis }
                .groupBy { message ->
                    getFormattedDate(message.dateInMillis)
                }
            return messagesByDate.flatMap { (date, messages) ->
                listOf(DateSeparatorUI(date)) + parseMessages(messages)
            }
        }

        private fun getFormattedDate(dateOfMessageInMillis: Long): String {
            val formatter = SimpleDateFormat("dd MMMM")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateOfMessageInMillis
            return formatter.format(calendar.time)
        }

        private fun parseMessages(remoteMessages: List<FakeServerApi.Message>): List<ViewTyped> {
            val messageUIList = mutableListOf<ViewTyped>()
            remoteMessages.forEach { messageToken ->
                if (messageToken.uid == MY_USER_ID) {
                    messageUIList.add(
                        OutMessageUI(
                            userName = fakeServer.getUserNameById(messageToken.uid),
                            message = messageToken.message,
                            reactions = messageToken.reactions,
                            dateInMillis = messageToken.dateInMillis,
                            uid = messageToken.uid,
                        )
                    )
                } else {
                    messageUIList.add(
                        InMessageUI(
                            userName = fakeServer.getUserNameById(messageToken.uid),
                            message = messageToken.message,
                            reactions = messageToken.reactions,
                            dateInMillis = messageToken.dateInMillis,
                            uid = messageToken.uid,
                        )
                    )
                }
            }
            return messageUIList
        }
    }

    private lateinit var chatListBinding: ChatListBinding
    private lateinit var dialogBinding: BottomSheetBinding

    private lateinit var dialog: BottomSheetDialog
    private var dateOfClickedMessageInMillis: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatListBinding = ChatListBinding.inflate(layoutInflater)
        setContentView(chatListBinding.root)
        chatListBinding.ImageViewSendMessage.clipToOutline = true

        dialog = BottomSheetDialog(this)
        dialogBinding = BottomSheetBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.bottomSheet)
        fillTextViewWithEmojisAsSpannableText(
            textView = dialogBinding.emojiListTextView,
            emojiCodeRange = 0x1F600..0x1F645
        )

        val clickListener = { clickedView: View ->
            when (clickedView) {
                is MessageViewGroup -> {
                    dateOfClickedMessageInMillis = clickedView.dateInMillis
                    dialog.show()
                }
                //ImageView that adds Emoji("+"), it are located in FlexBoxLayout, and FlexBox located in MessageViewGroup
                is ImageView -> {
                    val flexBoxLayout = clickedView.parent as FlexboxLayout
                    val messageViewGroup = flexBoxLayout.parent as MessageViewGroup
                    dateOfClickedMessageInMillis = messageViewGroup.dateInMillis
                    dialog.show()
                }
            }
        }

        val holderFactory = ChatHolderFactory(clickListener)
        val diffCallBack = DiffCallbackMessageUI()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        chatListBinding.recycleView.adapter = asyncAdapter
        viewTypedList = getMessageItemsFromFakeServer().toMutableList()
        asyncAdapter.items.submitList(getMessageItemsFromFakeServer())

        chatListBinding.ImageViewSendMessage.setOnClickListener {
            if (chatListBinding.EditTextEnterMessage.text.isNotEmpty()) {
                viewTypedList.add(
                    OutMessageUI(
                        userName = "Sibagatullin Artur",
                        message = chatListBinding.EditTextEnterMessage.text.toString(),
                        reactions = listOf(),
                        dateInMillis = Calendar.getInstance().timeInMillis,
                        uid = MY_USER_ID
                    )
                )
                chatListBinding.EditTextEnterMessage.text.clear()
                //!!!
                sendMessagesToFakeServer(viewTypedList)
                val refreshedList = getMessageItemsFromFakeServer()
                asyncAdapter.items.submitList(refreshedList) {
                    chatListBinding.recycleView.smoothScrollToPosition(
                        asyncAdapter.itemCount
                    )
                }
            }
        }

        chatListBinding.EditTextEnterMessage.addTextChangedListener(object : TextWatcher {
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
                    0 -> chatListBinding.ImageViewSendMessage.setImageResource(R.drawable.enter_message_button)
                    1 -> chatListBinding.ImageViewSendMessage.setImageResource(R.drawable.send_message_picture)
                }
            }

            override fun afterTextChanged(s: Editable?) {
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

    inner class MyClickableSpan(private val start: Int, private val end: Int) :
        ClickableSpan() {

        override fun onClick(widget: View) {
            val emojiCodeString = (widget as TextView).text.subSequence(start, end).toString()
            val emojiCode = emojiCodeString.codePointAt(0)
            val handlerAddingNewReaction = { reactions: MutableList<FakeServerApi.Reaction> ->
                reactions.add(FakeServerApi.Reaction(emojiCode, 1, listOf(MY_USER_ID)))
            }
            updateReactionsOfMessages(
                dateOfClickedMessageInMillis,
                handlerAddingNewReaction
            )
            dialog.dismiss()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }
}

