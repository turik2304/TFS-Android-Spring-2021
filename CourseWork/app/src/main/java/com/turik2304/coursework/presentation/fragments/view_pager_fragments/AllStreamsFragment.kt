package com.turik2304.coursework.presentation.fragments.view_pager_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.R
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.data.repository.ZulipRepository.toViewTypedItems
import com.turik2304.coursework.databinding.FragmentAllStreamsBinding
import com.turik2304.coursework.databinding.FragmentChannelsBinding
import com.turik2304.coursework.databinding.FragmentSubscribedBinding
import com.turik2304.coursework.domain.StreamsMiddleware
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.GeneralActions
import com.turik2304.coursework.presentation.GeneralReducer
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.base.UiState
import com.turik2304.coursework.presentation.recycler_view.AsyncAdapter
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.holder_factories.MainHolderFactory
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.Search
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AllStreamsFragment : MviFragment<GeneralActions, UiState>() {

    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var listOfStreams: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

    override val store: Store<GeneralActions, UiState> =
        Store(
            reducer = GeneralReducer(),
            middlewares = listOf(StreamsMiddleware(needAllStreams = true)),
            initialState = UiState()
        )
    override val actions: PublishRelay<GeneralActions> = PublishRelay.create()

    private val compositeDisposable = CompositeDisposable()

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
        _parentBinding = parentFragment?.let { FragmentChannelsBinding.bind(it.requireView()) }

        val divider = DividerItemDecoration(
            context,
            (binding.recycleViewAllStreams.layoutManager as LinearLayoutManager).orientation
        )
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_stream_separator, context?.theme)
        drawable?.let { divider.setDrawable(it) }
        binding.recycleViewAllStreams.addItemDecoration(divider)

        val clickListener = { clickedView: View ->
            when (clickedView) {
                is ImageView -> {
                    clickedView.setImageResource(R.drawable.ic_arrow_up_24)
                }
                is LinearLayout -> {
                    val intent = Intent(context, ChatActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        binding.recycleViewAllStreams.adapter = asyncAdapter

        compositeDisposable += store.wire()
        compositeDisposable += store.bind(this)
        actions.accept(GeneralActions.LoadItems)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _parentBinding = null
    }

    override fun render(state: UiState) {
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
            Search.initSearch(parentBinding.edSearchStreams, binding.recycleViewAllStreams)
        }
    }
}