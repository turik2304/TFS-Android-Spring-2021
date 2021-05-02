package com.turik2304.coursework.presentation.base

data class UiState(
    val isLoading: Boolean = false,
    val data: Any? = null,
    val error: Throwable? = null,
): State