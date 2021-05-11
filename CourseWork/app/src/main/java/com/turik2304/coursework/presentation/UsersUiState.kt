package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.State
import com.turik2304.coursework.presentation.recycler_view.items.UserUI

data class UsersUiState(
    val isLoading: Boolean = false,
    val data: Any? = null,
    val userInfo: UserUI? = null,
    val error: Throwable? = null,
) : State