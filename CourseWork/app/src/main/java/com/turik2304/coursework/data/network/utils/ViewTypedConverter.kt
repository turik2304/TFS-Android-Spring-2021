package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.network.models.PreViewTyped
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

interface ViewTypedConverter {
    val messageHelper: MessageHelper
    fun <T : PreViewTyped> convertToViewTypedItems(modelList: List<T>): List<ViewTyped>
}