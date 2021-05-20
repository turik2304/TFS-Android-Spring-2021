package com.turik2304.coursework.presentation.recycler_view.clicks

import com.turik2304.coursework.data.EmojiEnum
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.recycler_view.items.BottomSheetReactionUI
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class ChatClickMapper(
    private val outMessageClick: Observable<OutMessageUI>,
    private val inMessageClick: Observable<InMessageUI>,
    private val bottomSheetReactionClick: Observable<BottomSheetReactionUI>
) : ClickMapper<ChatActions> {

    private var uidOfClickedMessage: Int = -1

    override fun bind(actionConsumer: Consumer<ChatActions>): Disposable {
        return Observable.merge(
            outMessageClick.map {
                uidOfClickedMessage = it.uid
                ChatActions.ShowBottomSheetDialog
            },
            inMessageClick.map {
                uidOfClickedMessage = it.uid
                ChatActions.ShowBottomSheetDialog
            },
            bottomSheetReactionClick.map { reaction ->
                val nameAndZulipEmojiCode =
                    EmojiEnum.getNameAndCodeByCodePoint(reaction.emojiCode)
                val zulipEmojiName = nameAndZulipEmojiCode.first
                val zulipEmojiCode = nameAndZulipEmojiCode.second
                ChatActions.AddReaction(
                    messageId = uidOfClickedMessage,
                    emojiName = zulipEmojiName,
                    emojiCode = zulipEmojiCode
                )
            }
        ).subscribe { action ->
            actionConsumer.accept(action)
        }
    }
}