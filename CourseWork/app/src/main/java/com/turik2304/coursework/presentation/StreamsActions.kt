package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI

sealed class StreamsActions : Action {

    object LoadStreams : StreamsActions()

    class StreamsLoaded(val items: Any) : StreamsActions()

    object LoadedEmptyList: StreamsActions()

    class ErrorLoading(val error: Throwable) : StreamsActions()

    class ExpandStream(val stream: StreamUI) : StreamsActions()

    object StreamExpanded: StreamsActions()

    class ReduceStream(val stream: StreamUI) : StreamsActions()

    object StreamReduced: StreamsActions()

    class OpenChat(val nameOfTopic: String, val nameOfStream: String) : StreamsActions()

    object ChatOpened: StreamsActions()

}