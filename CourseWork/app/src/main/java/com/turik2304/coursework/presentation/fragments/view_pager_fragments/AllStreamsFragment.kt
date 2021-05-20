package com.turik2304.coursework.presentation.fragments.view_pager_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.MyApp
import com.turik2304.coursework.R
import com.turik2304.coursework.databinding.FragmentAllStreamsBinding
import com.turik2304.coursework.databinding.FragmentChannelsBinding
import com.turik2304.coursework.di.modules.StreamsModule
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.StreamsActions
import com.turik2304.coursework.presentation.StreamsUiState
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.base.Recycler
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.clicks.StreamsClickMapper
import com.turik2304.coursework.presentation.recycler_view.holder_factories.MainHolderFactory
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.Search
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

class AllStreamsFragment : MviFragment<StreamsActions, StreamsUiState>() {

    @field:[Inject Named(StreamsModule.ALL_STREAMS_STORE)]
    override lateinit var store: Store<StreamsActions, StreamsUiState>

    @Inject
    override lateinit var actions: PublishRelay<StreamsActions>

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var recycler: Recycler<ViewTyped>
    private lateinit var listOfStreams: List<StreamUI>
    private val listOfExpandedStreams = mutableListOf<Int>()

    private var _binding: FragmentAllStreamsBinding? = null
    private var _parentBinding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val parentBinding get() = _parentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllStreamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.application as MyApp).streamsComponent?.inject(this)
        _parentBinding = parentFragment?.let { FragmentChannelsBinding.bind(it.requireView()) }

        initRecycler()
        initRecyclerClicks()

        compositeDisposable += store.wire()
        compositeDisposable += store.bind(this)
        actions.accept(StreamsActions.LoadStreams)
    }

    override fun onStart() {
        super.onStart()
        (activity?.application as MyApp).clearChatComponent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
        _parentBinding = null
    }

    override fun render(state: StreamsUiState) {
        renderLoading(state.isLoading)
        renderError(state.error)
        renderLoadedStreams(state.data)
        renderExpandedStream(state.expandStream)
        renderReducedStream(state.reduceStream)
        renderOpeningChat(state)
    }

    private fun renderLoading(isLoading: Boolean) {
        if (isLoading) {
            parentBinding.tabLayoutShimmer.showShimmer(true)
        } else {
            parentBinding.tabLayoutShimmer.stopAndHideShimmer()
        }
    }

    private fun renderError(error: Throwable?) {
        error?.let { Error.showError(context, it) }
    }

    private fun renderLoadedStreams(streams: List<ViewTyped>?) {
        streams?.let {
            listOfStreams = it as List<StreamUI>
            updateList()
        }
    }

    private fun renderExpandedStream(expandableStream: StreamUI?) {
        expandableStream?.let { expandedStream ->
            if (expandedStream.uid !in listOfExpandedStreams) {
                listOfExpandedStreams.add(expandedStream.uid)
                updateList()
                actions.accept(StreamsActions.StreamExpanded)
            } else {
                actions.accept(StreamsActions.ReduceStream(expandedStream))
            }
        }
    }

    private fun renderReducedStream(reducibleStream: StreamUI?) {
        reducibleStream?.let { reducedStream ->
            listOfExpandedStreams.remove(reducedStream.uid)
            updateList()
            actions.accept(StreamsActions.StreamReduced)
        }
    }

    private fun renderOpeningChat(state: StreamsUiState) {
        if (state.nameOfTopic != null && state.nameOfStream != null) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_NAME_OF_TOPIC, state.nameOfTopic)
            intent.putExtra(ChatActivity.EXTRA_NAME_OF_STREAM, state.nameOfStream)
            startActivity(intent)
            (activity?.application as MyApp).addChatComponent()
            actions.accept(StreamsActions.ChatOpened)
        }
    }

    private fun initRecycler() {
        val divider = DividerItemDecoration(
            context,
            (binding.recycleViewAllStreams.layoutManager as LinearLayoutManager).orientation
        )
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_stream_separator, context?.theme)
        drawable?.let { divider.setDrawable(it) }
        recycler = Recycler(
            recyclerView = binding.recycleViewAllStreams,
            diffCallback = DiffCallback<ViewTyped>(),
            holderFactory = MainHolderFactory(),
        ) {
            itemDecoration += divider
        }
    }

    private fun initRecyclerClicks() {
        val streamClick = recycler.clickedItem<StreamUI>(R.layout.item_stream)
        val topicClick = recycler.clickedItem<TopicUI>(R.layout.item_topic)
        compositeDisposable += StreamsClickMapper(
            streamClick = streamClick,
            topicClick = topicClick
        ).bind(actions)
    }

    private fun updateList() {
        val updatedList = listOfStreams.flatMap { stream ->
            if (stream.uid in listOfExpandedStreams) {
                listOf(stream.copy(isExpanded = true)) + stream.topics
            } else listOf(stream.copy(isExpanded = false))
        }
        recycler.setItems(updatedList)
        Search.initSearch(parentBinding.edSearchStreams, binding.recycleViewAllStreams)
    }
}