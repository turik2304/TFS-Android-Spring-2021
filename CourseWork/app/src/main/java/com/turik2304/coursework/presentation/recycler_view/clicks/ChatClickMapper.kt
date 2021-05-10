package com.turik2304.coursework.presentation.recycler_view.clicks

import android.util.Log
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class ChatClickMapper(
    private val outMessageClick: Observable<OutMessageUI>,
    private val inMessageClick: Observable<InMessageUI>
) {

    fun bind(actionConsumer: Consumer<ChatActions>): Disposable {
        return Observable.merge(
            outMessageClick.map {
                Log.d("xxx", "click")
                ChatActions.ShowBottomSheetDialog(
                    uidOfClickedMessage = it.uid
                )
            },
            inMessageClick.map {
                ChatActions.ShowBottomSheetDialog(
                    uidOfClickedMessage = it.uid
                )
            }
        ).subscribe { action ->
            actionConsumer.accept(action)
        }
    }
}