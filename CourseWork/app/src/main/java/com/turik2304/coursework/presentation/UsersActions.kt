package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.recycler_view.items.UserUI

sealed class UsersActions : Action {

    object LoadUsers : UsersActions()

    class UsersLoaded(val items: Any) : UsersActions()

    object LoadedEmptyList : UsersActions()

    class ErrorLoading(val error: Throwable) : UsersActions()

    class OpenUserInfo(val user: UserUI) : UsersActions()

}