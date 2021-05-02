package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Reducer
import com.turik2304.coursework.presentation.base.UiState

class UsersReducer : Reducer<UiState, GeneralActions> {
    override fun reduce(state: UiState, action: GeneralActions): UiState {
        return when (action) {
            is GeneralActions.LoadItems -> state.copy(isLoading = true)
            is GeneralActions.ItemsLoaded -> state.copy(isLoading = false, data = action.items)
            is GeneralActions.ErrorLoading -> state.copy(isLoading = false, error = action.error)
        }
    }

}