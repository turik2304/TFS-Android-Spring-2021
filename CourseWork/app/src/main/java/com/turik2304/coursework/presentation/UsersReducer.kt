package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Reducer
import com.turik2304.coursework.presentation.recycler_view.items.UserUI

class UsersReducer : Reducer<UsersUiState, UsersActions> {
    override fun reduce(state: UsersUiState, action: UsersActions): UsersUiState {
        return when (action) {
            is UsersActions.LoadUsers -> UsersUiState(
                isLoading = true
            )
            is UsersActions.UsersLoaded -> UsersUiState(
                data = action.items
            )
            is UsersActions.ErrorLoading -> UsersUiState(
                isLoading = false,
                error = action.error
            )
            is UsersActions.OpenUserInfo -> UsersUiState(
                userInfo = action.user
            )
        }
    }

}