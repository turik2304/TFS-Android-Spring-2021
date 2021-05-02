package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

sealed class GeneralActions: Action {

    object LoadItems : GeneralActions()

    data class ItemsLoaded(val items: List<ViewTyped>) : GeneralActions()

    data class ErrorLoading(val error: Throwable) : GeneralActions()

}