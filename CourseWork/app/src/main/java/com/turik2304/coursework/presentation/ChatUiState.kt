package com.turik2304.coursework.presentation

import com.turik2304.coursework.data.network.models.data.MessageData
import com.turik2304.coursework.presentation.base.State

data class ChatUiState(
    val isLoading: Boolean = false,
    val isFirstPage: Boolean = false,
    val messageClicked: Boolean = false,
    val data: MessageData? = null,
    val auxiliaryData: List<*>? = null,
    val error: Throwable? = null,
) : State

