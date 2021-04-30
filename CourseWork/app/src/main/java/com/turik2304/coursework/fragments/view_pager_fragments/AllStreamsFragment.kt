package com.turik2304.coursework.fragments.view_pager_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.Error
import com.turik2304.coursework.R
import com.turik2304.coursework.Search
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.network.ZulipRepository
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AllStreamsFragment : Fragment() {

    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var listOfStreams: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_streams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editText = parentFragment?.view?.findViewById<EditText>(R.id.edSearchStreams)
        val recyclerViewAllStreams = view.findViewById<RecyclerView>(R.id.recycleViewAllStreams)
        val tabLayoutShimmer =
            parentFragment?.view?.findViewById<ShimmerFrameLayout>(R.id.tabLayoutShimmer)
        tabLayoutShimmer?.showShimmer(true)

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

        val asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewAllStreams.adapter = asyncAdapter
        compositeDisposable +=
            ZulipRepository.getStreams(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamList ->
                        innerViewTypedList = streamList
                        asyncAdapter.items.submitList(streamList)
                        Search.initSearch(
                            editText,
                            recyclerViewAllStreams
                        )
                        tabLayoutShimmer?.stopAndHideShimmer()
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        tabLayoutShimmer?.stopAndHideShimmer()
                    })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}