package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.base.Reducer
import com.turik2304.coursework.presentation.base.UiState

class UsersReducer : Reducer<UiState, Action> {
    override fun reduce(state: UiState, action: Action): UiState {
        return when (action) {
            is Action.LoadItems -> state.copy(isLoading = true)
            is Action.ItemsLoaded -> state.copy(isLoading = false, data = action.items)
            is Action.ErrorLoading -> state.copy(isLoading = false, error = action.error)
        }
    }

}