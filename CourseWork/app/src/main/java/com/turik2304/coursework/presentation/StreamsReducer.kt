package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Reducer

class StreamsReducer : Reducer<StreamsUiState, StreamsActions> {
    override fun reduce(state: StreamsUiState, action: StreamsActions): StreamsUiState {
        return when (action) {
            is StreamsActions.LoadStreams -> StreamsUiState(
                isLoading = true
            )
            is StreamsActions.StreamsLoaded -> StreamsUiState(
                isLoading = false,
                data = action.items
            )
            is StreamsActions.LoadedEmptyList -> StreamsUiState(
                isLoading = true
            )
            is StreamsActions.ErrorLoading -> StreamsUiState(
                isLoading = false,
                error = action.error
            )
            is StreamsActions.ExpandStream -> StreamsUiState(
                isLoading = true,
                expandStream = action.stream
            )
            is StreamsActions.StreamExpanded -> StreamsUiState(
                isLoading = false
            )
            is StreamsActions.ReduceStream -> StreamsUiState(
                isLoading = true,
                reduceStream = action.stream
            )
            is StreamsActions.StreamReduced -> StreamsUiState(
                isLoading = false
            )
            is StreamsActions.OpenChat -> StreamsUiState(
                isLoading = true,
                nameOfTopic = action.nameOfTopic,
                nameOfStream = action.nameOfStream
            )
            is StreamsActions.ChatOpened -> StreamsUiState(
                isLoading = false
            )
        }
    }

}