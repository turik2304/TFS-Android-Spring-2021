package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Reducer

class GeneralReducer : Reducer<GeneralUiState, GeneralActions> {
    override fun reduce(state: GeneralUiState, action: GeneralActions): GeneralUiState {
        return when (action) {
            is GeneralActions.LoadItems -> state.copy(
                isLoading = true,
                data = null,
                error = null
            )
            is GeneralActions.ItemsLoaded -> state.copy(
                isLoading = false,
                data = action.items,
                error = null
            )
            is GeneralActions.ErrorLoading -> state.copy(
                isLoading = false,
                error = action.error
            )
        }
    }

}