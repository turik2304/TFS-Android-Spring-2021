package com.turik2304.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpannable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.turik2304.coursework.databinding.BottomSheetBinding
import com.turik2304.coursework.databinding.ChatListBinding
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.diff_utils.DiffCallbackMessageUI
import com.turik2304.coursework.recycler_view_base.items.DateSeparatorUI
import com.turik2304.coursework.recycler_view_base.items.InMessageUI
import com.turik2304.coursework.recycler_view_base.items.OutMessageUI
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var innerViewTypedList: MutableList<ViewTyped>
        lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
        private val api: ServerApi = FakeServerApi()
        fun updateReactionsOfMessages(
            uidOfMessage: String,
            handler: (MutableList<ServerApi.Reaction>) -> Boolean
        ) {
            innerViewTypedList.forEach { item ->
                when (item.viewType) {
                    R.layout.item_incoming_message -> {
                        item as InMessageUI
                        if (item.uid == uidOfMessage) {
                            val reactions = item.reactions.toMutableList()
                            handler(reactions)
                            item.reactions = reactions
                        }
                    }
                    R.layout.item_outcoming_message -> {
                        item as OutMessageUI
                        if (item.uid == uidOfMessage) {
                            val reactions = item.reactions.toMutableList()
                            handler(reactions)
                            item.reactions = reactions
                        }
                    }
                }
            }
            //!!!
            sendMessagesToFakeServer(innerViewTypedList)
            val refreshedList = getMessageItemsFromFakeServer()
            asyncAdapter.items.submitList(refreshedList) {

            }
        }

        private fun sendMessagesToFakeServer(viewTypedList: List<ViewTyped>) {
            val messages = mutableListOf<ServerApi.Message>()
            viewTypedList.forEach { item ->
                when (item.viewType) {
                    R.layout.item_incoming_message -> {
                        item as InMessageUI
                        messages.add(
                            ServerApi.Message(
                                message = item.message,
                                dateInMillis = item.dateInMillis,
                                userId = item.userId,
                                reactions = item.reactions,
                                uid = item.uid
                            )
                        )
                    }
                    R.layout.item_outcoming_message -> {
                        item as OutMessageUI
                        messages.add(
                            ServerApi.Message(
                                message = item.message,
                                dateInMillis = item.dateInMillis,
                                userId = item.userId,
                                reactions = item.reactions,
                                uid = item.uid
                            )
                        )
                    }
                }
            }
            api.sendMessages(messages)
        }

        private fun getMessageItemsFromFakeServer(): List<ViewTyped> {
            val messagesByDate = api.getMessages()
                .sortedBy { it.dateInMillis }
                .groupBy { message ->
                    getFormattedDate(message.dateInMillis)
                }
            return messagesByDate.flatMap { (date, messages) ->
                listOf(DateSeparatorUI(date, "DATE_SEPARATOR_$date")) + parseMessages(messages)
            }
        }

        private fun getFormattedDate(dateOfMessageInMillis: Long): String {
            val formatter = SimpleDateFormat("dd MMMM")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = dateOfMessageInMillis
            return formatter.format(calendar.time)
        }

        private fun parseMessages(remoteMessages: List<ServerApi.Message>): List<ViewTyped> {
            val messageUIList = mutableListOf<ViewTyped>()
            remoteMessages.forEach { messageToken ->
                if (messageToken.userId == MyUserId.MY_USER_ID) {
                    messageUIList.add(
                        OutMessageUI(
                            userName = api.getUserNameById(messageToken.userId),
                            userId = messageToken.userId,
                            message = messageToken.message,
                            reactions = messageToken.reactions,
                            dateInMillis = messageToken.dateInMillis,
                            uid = messageToken.uid,
                        )
                    )
                } else {
                    messageUIList.add(
                        InMessageUI(
                            userName = api.getUserNameById(messageToken.userId),
                            userId = messageToken.userId,
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
    private var uidOfClickedMessageInMillis: String = ""


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
                    uidOfClickedMessageInMillis = clickedView.uid
                    dialog.show()
                }
                //ImageView that adds Emoji("+"), it are located in FlexBoxLayout, and FlexBox located in MessageViewGroup
                is ImageView -> {
                    val flexBoxLayout = clickedView.parent as FlexboxLayout
                    val messageViewGroup = flexBoxLayout.parent as MessageViewGroup
                    uidOfClickedMessageInMillis = messageViewGroup.uid
                    dialog.show()
                }
            }
        }

        val holderFactory = ChatHolderFactory(clickListener)
        val diffCallBack = DiffCallbackMessageUI()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        chatListBinding.recycleView.adapter = asyncAdapter
        innerViewTypedList = getMessageItemsFromFakeServer().toMutableList()
        asyncAdapter.items.submitList(getMessageItemsFromFakeServer())

        chatListBinding.ImageViewSendMessage.setOnClickListener {
            if (chatListBinding.EditTextEnterMessage.text.isNotEmpty()) {
                innerViewTypedList.add(
                    OutMessageUI(
                        userName = "Sibagatullin Artur",
                        userId = MyUserId.MY_USER_ID,
                        uid = Random().nextInt().toString(),
                        message = chatListBinding.EditTextEnterMessage.text.toString(),
                        reactions = listOf(),
                        dateInMillis = Calendar.getInstance().timeInMillis,
                    )
                )
                chatListBinding.EditTextEnterMessage.text.clear()
                //!!!
                sendMessagesToFakeServer(innerViewTypedList)
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
            val handlerAddingNewReaction = { reactions: MutableList<ServerApi.Reaction> ->
                reactions.add(ServerApi.Reaction(emojiCode, 1, listOf(MyUserId.MY_USER_ID)))
            }
            updateReactionsOfMessages(
                uidOfClickedMessageInMillis,
                handlerAddingNewReaction
            )
            dialog.dismiss()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }
}

