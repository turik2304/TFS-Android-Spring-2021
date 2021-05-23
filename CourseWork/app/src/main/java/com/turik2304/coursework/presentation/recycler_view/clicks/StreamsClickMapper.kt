package com.turik2304.coursework.presentation.recycler_view.clicks

import com.turik2304.coursework.presentation.StreamsActions
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class StreamsClickMapper(
    private val streamClick: Observable<StreamUI>,
    private val topicClick: Observable<TopicUI>
) : ClickMapper<StreamsActions> {

    override fun bind(actionConsumer: Consumer<StreamsActions>): Disposable {
        return Observable.merge(
            streamClick.map {
                StreamsActions.ExpandStream(
                    stream = it
                )
            },
            topicClick.map {
                StreamsActions.OpenChat(
                    nameOfTopic = it.nameOfTopic,
                    nameOfStream = it.nameOfStream
                )
            }
        ).subscribe { action ->
            actionConsumer.accept(action)
        }
    }
}