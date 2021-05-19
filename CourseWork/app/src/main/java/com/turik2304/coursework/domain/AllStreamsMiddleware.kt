package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.presentation.StreamsActions
import com.turik2304.coursework.presentation.StreamsUiState
import io.reactivex.rxjava3.core.Observable

class AllStreamsMiddleware(override val repository: Repository) :
    Middleware<StreamsActions, StreamsUiState> {

//    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<StreamsActions>,
        state: Observable<StreamsUiState>
    ): Observable<StreamsActions> {
        return actions.ofType(StreamsActions.LoadStreams::class.java)
            .flatMap {
                return@flatMap repository.getStreams(needAllStreams = true)
                    .map<StreamsActions> { result ->
                        val streams = repository.converter.convertToViewTypedItems(result)
                        return@map if (streams.isEmpty()) StreamsActions.LoadedEmptyList
                        else StreamsActions.StreamsLoaded(streams)
                    }
                    .onErrorReturn { error -> StreamsActions.ErrorLoading(error) }
            }
    }
}