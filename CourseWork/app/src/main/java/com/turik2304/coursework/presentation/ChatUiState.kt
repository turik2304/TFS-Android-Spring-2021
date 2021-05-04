package com.turik2304.coursework.presentation

import com.turik2304.coursework.data.network.models.data.LoadedData
import com.turik2304.coursework.presentation.base.State

data class ChatUiState(
    val isLoading: Boolean = false,
    val isFirstPage: Boolean = false,
    val data: LoadedData? = null,
    val error: Throwable? = null,
) : State

