package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Reducer

class ChatReducer : Reducer<ChatUiState, ChatActions> {
    override fun reduce(state: ChatUiState, action: ChatActions): ChatUiState {
        return when (action) {
            is ChatActions.LoadItems -> {
                state.copy(
                    isLoading = true,
                    isFirstPage = action.needFirstPage,
                    data = null,
                    error = null
                )
            }
            is ChatActions.ItemsLoaded -> state.copy(
                isLoading = false,
                isFirstPage = action.isFirstPage,
                data = action.items,
                error = null
            )
            is ChatActions.LoadedEmptyList -> state.copy(
                isLoading = false,
                data = null,
                error = null
            )
            is ChatActions.ErrorLoading -> state.copy(
                isLoading = false,
                error = action.error
            )
            is ChatActions.AddReaction -> TODO()
            is ChatActions.RemoveReaction -> TODO()
            is ChatActions.SendMessage -> TODO()
        }
    }

}