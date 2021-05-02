package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action

sealed class GeneralActions: Action {

    object LoadItems : GeneralActions()

    class ItemsLoaded(val items: Any) : GeneralActions()

    class ErrorLoading(val error: Throwable) : GeneralActions()

}