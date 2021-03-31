package com.turik2304.coursework.fragments.view_pager_fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.*
import com.turik2304.coursework.ChatActivity.Companion.EXTRA_NAME_OF_STREAM
import com.turik2304.coursework.ChatActivity.Companion.EXTRA_NAME_OF_TOPIC
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class SubscribedFragment : Fragment() {

    private lateinit var innerViewTypedList: MutableList<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

    private val api: ServerApi = FakeServerApi()

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
        val clickListener = clickListener@{ clickedView: View ->
            val positionOfClickedView =
                recyclerViewSubscribedStreams.getChildAdapterPosition(clickedView)
            val clickedItem = asyncAdapter.items.currentList[positionOfClickedView]
            if (clickedItem.viewType == R.layout.item_stream) {
                val streamShimmer =
                    clickedView as ShimmerFrameLayout
                streamShimmer.showShimmer(true)
                val expandImageView = clickedView.findViewById<ImageView>(R.id.imExpandStream)
                val uidOfClickedStreamUI = clickedItem.uid
                var indexOfTopicInsertion = -1
                var deleteTopics = false
                innerViewTypedList.forEachIndexed { index, viewTypedUI ->
                    if (viewTypedUI.uid == uidOfClickedStreamUI &&
                        viewTypedUI is StreamUI &&
                        !viewTypedUI.isExpanded
                    ) {
                        viewTypedUI.isExpanded = true
                        indexOfTopicInsertion = index + 1
                    } else if (viewTypedUI.uid == uidOfClickedStreamUI &&
                        viewTypedUI is StreamUI &&
                        viewTypedUI.isExpanded
                    ) {
                        viewTypedUI.isExpanded = false
                        indexOfTopicInsertion = index + 1
                        deleteTopics = true
                    }
                }
                try {
                    if (deleteTopics) {
                        while (innerViewTypedList[indexOfTopicInsertion] is TopicUI) {
                            repeat(2) {
                                innerViewTypedList.removeAt(indexOfTopicInsertion)
                            }
                            if (indexOfTopicInsertion == innerViewTypedList.size) break
                            asyncAdapter.items.submitList(innerViewTypedList.map { it })
                            animateExpandImageViewFrom180to0(context, expandImageView)
                        }
                        streamShimmer.stopAndHideShimmer()
                        clickedView.setBackgroundColor(
                            resources.getColor(
                                R.color.gray_primary_background,
                                context?.theme
                            )
                        )
                    } else {
                        api.getTopicsUIListByStreamUid(uidOfClickedStreamUI)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { listOfTopics ->
                                    innerViewTypedList.addAll(indexOfTopicInsertion, listOfTopics)
                                    asyncAdapter.items.submitList(innerViewTypedList.map { it })
                                    animateExpandImageViewFrom0to180(context, expandImageView)
                                    streamShimmer.stopAndHideShimmer()
                                    clickedView.setBackgroundColor(
                                        resources.getColor(
                                            R.color.gray_secondary_background,
                                            context?.theme
                                        )
                                    )
                                },
                                { onError ->
                                    Error.showError(
                                        context,
                                        onError
                                    )
                                    streamShimmer.stopAndHideShimmer()
                                })

                    }
                } catch (e: IndexOutOfBoundsException) {
                    streamShimmer.stopAndHideShimmer()
                    Toast.makeText(
                        context,
                        resources.getString(R.string.errorExpandStream),
                        Toast.LENGTH_SHORT
                    ).show()
                }

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

        api.getStreamUIListFromServer(needAllStreams = false)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { streamList ->
                    asyncAdapter.items.submitList(streamList)
                    innerViewTypedList = streamList.toMutableList()
                    Search.initSearch(
                        editText,
                        innerViewTypedList,
                        asyncAdapter,
                        recyclerViewSubscribedStreams
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
}