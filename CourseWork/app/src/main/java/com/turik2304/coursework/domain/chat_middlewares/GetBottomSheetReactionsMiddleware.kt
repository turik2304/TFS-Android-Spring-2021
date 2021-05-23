package com.turik2304.coursework.domain.chat_middlewares

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class GetBottomSheetReactionsMiddleware(override val repository: Repository) :
    Middleware<ChatActions, ChatUiState> {

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.GetBottomSheetReactions::class.java)
            .flatMap {
                return@flatMap repository.getBottomSheetReactionList()
                    .map<ChatActions> { reactions ->
                        ChatActions.BottomSheetReactionsReceived(
                            bottomSheetReactions = repository.converter.convertToViewTypedItems(
                                reactions
                            )
                        )
                    }
                    .onErrorReturn { error -> ChatActions.ErrorLoading(error) }
            }
    }
}