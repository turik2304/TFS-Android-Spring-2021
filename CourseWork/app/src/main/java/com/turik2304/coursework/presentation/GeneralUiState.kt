package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.State

data class GeneralUiState(
    val isLoading: Boolean = false,
    val data: Any? = null,
    val error: Throwable? = null,
): State