package com.turik2304.coursework.fragments.view_pager_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.R
import com.turik2304.coursework.fragments.view_pager_fragments.SubscribedFragment.Companion.animateExpandImageViewFrom0to180
import com.turik2304.coursework.fragments.view_pager_fragments.SubscribedFragment.Companion.animateExpandImageViewFrom180to0
import com.turik2304.coursework.fragments.view_pager_fragments.SubscribedFragment.Companion.getTopicsUIListByStreamUid
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.StreamAndTopicSeparatorUI
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI

class AllStreamsFragment : Fragment() {

    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var listOfStreams: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
    private val fakeServer: ServerApi = FakeServerApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_all_streams, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewAllStreams = view.findViewById<RecyclerView>(R.id.recycleViewAllStreams)
        val listOfExpandedStreams = mutableListOf<String>()
        val clickListener = clickListener@{ clickedView: View ->
            val positionOfClickedView =
                recyclerViewAllStreams.getChildAdapterPosition(clickedView)
            val clickedItem = innerViewTypedList[positionOfClickedView]
            if (clickedItem.viewType == R.layout.item_stream) {
                val expandImageView = clickedView.findViewById<ImageView>(R.id.imExpandStream)
                val uidOfStreamUI = clickedItem.uid
                if (uidOfStreamUI in listOfExpandedStreams) {
                    listOfExpandedStreams.remove(uidOfStreamUI)
                    (clickedItem as StreamUI).isExpanded = false
                    animateExpandImageViewFrom180to0(context, expandImageView)
                    clickedView.setBackgroundColor(
                        resources.getColor(
                            R.color.gray_primary_background,
                            context?.theme
                        )
                    )
                } else {
                    listOfExpandedStreams.add(uidOfStreamUI)
                    (clickedItem as StreamUI).isExpanded = true
                    animateExpandImageViewFrom0to180(context, expandImageView)
                    clickedView.setBackgroundColor(
                        resources.getColor(
                            R.color.gray_secondary_background,
                            context?.theme
                        )
                    )
                }
                innerViewTypedList = listOfStreams.flatMap { stream ->
                    listOf(stream) + if (stream.uid in listOfExpandedStreams) {
                        getTopicsUIListByStreamUid(stream.uid, fakeServer)
                    } else emptyList()
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
                intent.putExtra(SubscribedFragment.EXTRA_NAME_OF_TOPIC, nameOfTopic)
                intent.putExtra(SubscribedFragment.EXTRA_NAME_OF_STREAM, nameOfStreamUI)
                startActivity(intent)
            }
            return@clickListener
        }
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()

        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewAllStreams.adapter = asyncAdapter
        innerViewTypedList = getAllStreamsFromFakeServer()
        listOfStreams = innerViewTypedList
        asyncAdapter.items.submitList(listOfStreams)
    }

    private fun getAllStreamsFromFakeServer(): List<ViewTyped> {
        return fakeServer.allStreams.flatMap { (streamName, uid) ->
            listOf(
                StreamUI(
                    streamName,
                    uid
                )
            ) + listOf(StreamAndTopicSeparatorUI(uid = "STREAM_SEPARATOR_$uid"))
        }

    }

}