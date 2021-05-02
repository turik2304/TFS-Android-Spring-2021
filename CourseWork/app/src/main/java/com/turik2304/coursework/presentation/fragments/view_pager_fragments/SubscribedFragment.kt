package com.turik2304.coursework.presentation.fragments.view_pager_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.*
import com.turik2304.coursework.ChatActivity.Companion.EXTRA_NAME_OF_STREAM
import com.turik2304.coursework.ChatActivity.Companion.EXTRA_NAME_OF_TOPIC
import com.turik2304.coursework.databinding.FragmentChannelsBinding
import com.turik2304.coursework.databinding.FragmentSubscribedBinding
import com.turik2304.coursework.domain.StreamsMiddleware
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.GeneralActions
import com.turik2304.coursework.presentation.GeneralReducer
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.GeneralUiState
import com.turik2304.coursework.presentation.recycler_view.AsyncAdapter
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.holder_factories.MainHolderFactory
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.Search
import io.reactivex.rxjava3.disposables.CompositeDisposable


class SubscribedFragment : MviFragment<GeneralActions, GeneralUiState>() {

    private lateinit var listOfStreams: List<StreamUI>
    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

    override val store: Store<GeneralActions, GeneralUiState> = Store(
        reducer = GeneralReducer(),
        middlewares = listOf(StreamsMiddleware(needAllStreams = false)),
        initialState = GeneralUiState()
    )
    override val actions: PublishRelay<GeneralActions> = PublishRelay.create()

    private val compositeDisposable = CompositeDisposable()

    private var _binding: FragmentSubscribedBinding? = null
    private var _parentBinding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val parentBinding get() = _parentBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _parentBinding = parentFragment?.let { FragmentChannelsBinding.bind(it.requireView()) }

        val divider = DividerItemDecoration(
            context,
            (binding.recycleViewSubscribedStreams.layoutManager as LinearLayoutManager).orientation
        )
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_stream_separator, context?.theme)
        drawable?.let { divider.setDrawable(it) }
        binding.recycleViewSubscribedStreams.addItemDecoration(divider)

        val listOfExpandedStreams = mutableListOf<Int>()
        val clickListener = clickListener@{ clickedView: View ->
            val positionOfClickedView =
                binding.recycleViewSubscribedStreams.getChildAdapterPosition(clickedView)
            val clickedItem = asyncAdapter.items.currentList[positionOfClickedView]
            if (clickedItem.viewType == R.layout.item_stream) {
                val expandImageView = clickedView.findViewById<ImageView>(R.id.imExpandStream)
                val uidOfClickedStreamUI = clickedItem.uid
                if (uidOfClickedStreamUI in listOfExpandedStreams) {
                    listOfExpandedStreams.remove(uidOfClickedStreamUI)
                    animateExpandImageViewFrom180to0(context, expandImageView)
                    clickedView.setBackgroundColor(
                        resources.getColor(R.color.gray_primary_background, context?.theme)
                    )
                } else {
                    listOfExpandedStreams.add(uidOfClickedStreamUI)
                    animateExpandImageViewFrom0to180(context, expandImageView)
                    clickedView.setBackgroundColor(
                        resources.getColor(R.color.gray_secondary_background, context?.theme)
                    )
                }
                innerViewTypedList = listOfStreams.flatMap { stream ->
                    listOf(stream) + if (stream.uid in listOfExpandedStreams) stream.topics else emptyList()
                }
                asyncAdapter.items.submitList(innerViewTypedList)
            } else {
                val uidOfClickedTopicUI = clickedItem.uid
                //some logic to start chat by topic ID
                var indexOfTopicUI = 0
                var indexOfStreamUI = 0
                innerViewTypedList.forEachIndexed { index, viewTyped ->
                    if (viewTyped.uid == uidOfClickedTopicUI) indexOfTopicUI = index
                }
                val nameOfTopic = (innerViewTypedList[indexOfTopicUI] as TopicUI).name
                while (innerViewTypedList[indexOfTopicUI].viewType != R.layout.item_stream) {
                    indexOfStreamUI = indexOfTopicUI
                    indexOfTopicUI--
                }
                val nameOfStreamUI = (innerViewTypedList[indexOfStreamUI - 1] as StreamUI).name
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(EXTRA_NAME_OF_TOPIC, nameOfTopic)
                intent.putExtra(EXTRA_NAME_OF_STREAM, nameOfStreamUI)
                startActivity(intent)
            }
            return@clickListener
        }

        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        binding.recycleViewSubscribedStreams.adapter = asyncAdapter

        compositeDisposable += store.wire()
        compositeDisposable += store.bind(this)
        actions.accept(GeneralActions.LoadItems)
    }

    companion object {
        fun animateExpandImageViewFrom0to180(context: Context?, imageView: ImageView) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.rotate_0_180)
            imageView.startAnimation(animation)
            imageView.setImageResource(R.drawable.ic_arrow_up_24)
        }

        fun animateExpandImageViewFrom180to0(context: Context?, imageView: ImageView) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.rotate_180_0)
            imageView.startAnimation(animation)
            imageView.setImageResource(R.drawable.ic_arrow_down_24)
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        asyncAdapter.items.currentList.forEach { item ->
            if (item is StreamUI) {
                item.isExpanded = false
            }
        }
        _binding = null
        _parentBinding = null
    }

    override fun render(state: GeneralUiState) {
        if (state.isLoading) {
            parentBinding.tabLayoutShimmer.showShimmer(true)
        } else {
            parentBinding.tabLayoutShimmer.stopAndHideShimmer()
        }
        if (state.error != null) {
            parentBinding.tabLayoutShimmer.stopAndHideShimmer()
            Error.showError(context, state.error)
        }
        if (state.data != null) {
            val streamList = state.data as List<StreamUI>
            asyncAdapter.items.submitList(streamList)
            listOfStreams = streamList
            innerViewTypedList = streamList
            Search.initSearch(parentBinding.edSearchStreams, binding.recycleViewSubscribedStreams)
        }
    }

}



