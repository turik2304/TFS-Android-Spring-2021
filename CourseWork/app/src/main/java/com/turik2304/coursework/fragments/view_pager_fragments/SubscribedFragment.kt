package com.turik2304.coursework.fragments.view_pager_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.*
import com.turik2304.coursework.ChatActivity.Companion.EXTRA_NAME_OF_STREAM
import com.turik2304.coursework.ChatActivity.Companion.EXTRA_NAME_OF_TOPIC
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.network.ZulipRepository
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable


class SubscribedFragment : Fragment() {

    private lateinit var listOfStreams: List<StreamUI>
    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscribed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editText = parentFragment?.view?.findViewById<EditText>(R.id.edSearchStreams)
        val tabLayoutShimmer =
            parentFragment?.view?.findViewById<ShimmerFrameLayout>(R.id.tabLayoutShimmer)
        tabLayoutShimmer?.startShimmer()

        val recyclerViewSubscribedStreams =
            view.findViewById<RecyclerView>(R.id.recycleViewSubscribedStreams)
        val divider = DividerItemDecoration(
            recyclerViewSubscribedStreams.context,
            (recyclerViewSubscribedStreams.layoutManager as LinearLayoutManager).orientation
        )
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.ic_stream_separator, context?.theme)
        if (drawable != null) {
            divider.setDrawable(drawable)
        }
        recyclerViewSubscribedStreams.addItemDecoration(divider)

        val listOfExpandedStreams = mutableListOf<Int>()
        val clickListener = clickListener@{ clickedView: View ->
            val positionOfClickedView =
                recyclerViewSubscribedStreams.getChildAdapterPosition(clickedView)
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
        recyclerViewSubscribedStreams.adapter = asyncAdapter

        compositeDisposable +=
            ZulipRepository.getStreams(needAllStreams = false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamList ->
                        asyncAdapter.items.submitList(streamList)
                        tabLayoutShimmer?.stopAndHideShimmer()
                        listOfStreams = streamList
                        innerViewTypedList = streamList
                        Search.initSearch(
                            editText,
                            recyclerViewSubscribedStreams
                        )
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        tabLayoutShimmer?.stopAndHideShimmer()
                    })
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
    }

}



