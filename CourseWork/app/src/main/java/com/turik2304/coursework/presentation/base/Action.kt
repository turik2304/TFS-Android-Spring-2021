package com.turik2304.coursework.presentation.base

import com.turik2304.coursework.recycler_view_base.ViewTyped

sealed class Action {

    object LoadItems : Action()

    data class ItemsLoaded(val items: List<ViewTyped>): Action()

    data class ErrorLoading(val error: Throwable): Action()

}