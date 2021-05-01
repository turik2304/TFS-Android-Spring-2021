package com.turik2304.coursework.presentation.base

import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

sealed class Action {

    object LoadItems : Action()

    data class ItemsLoaded(val items: List<ViewTyped>): Action()

    data class ErrorLoading(val error: Throwable): Action()

}