package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.State
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class ChatUiState(
    val isLoading: Boolean = false,
    val isFirstPage: Boolean = false,
    val data: List<ViewTyped>? = null,
    val error: Throwable? = null,
): State