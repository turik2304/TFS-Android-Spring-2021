package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.State
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI

data class StreamsUiState(
    val isLoading: Boolean = false,
    val data: List<ViewTyped>? = null,
    val expandStream: StreamUI? = null,
    val reduceStream: StreamUI? = null,
    val nameOfTopic: String? = null,
    val nameOfStream: String? = null,
    val error: Throwable? = null,
) : State