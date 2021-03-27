package com.turik2304.coursework.fragments.view_pager_fragments

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.R
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.StreamAndTopicSeparatorUI
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import java.lang.IndexOutOfBoundsException

class SubscribedFragment : Fragment() {

    private lateinit var innerViewTypedList: MutableList<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

    private val fakeServer: ServerApi = FakeServerApi()
    private var rotationAngle = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscribed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewSubscribedStreams =
            view.findViewById<RecyclerView>(R.id.recycleViewSubscribedStreams)

        val clickListener = clickListener@{ clickedView: View ->
            val positionOfClickedView =
                recyclerViewSubscribedStreams.getChildAdapterPosition(clickedView)
            val clickedItem = innerViewTypedList[positionOfClickedView]
            if (clickedItem.viewType == R.layout.item_stream) {
                val uidOfStreamUI = clickedItem.uid
                val expandImageView = clickedView.findViewById<ImageView>(R.id.imExpandStream)

                //load topics from fake server
                val topicUIList = getTopicsUIListByStreamUid(uidOfStreamUI)
                var indexOfTopicInsertion = -1
                var deleteTopics = false
                innerViewTypedList.forEachIndexed { index, viewTypedUI ->
                    if (viewTypedUI.uid == uidOfStreamUI &&
                        viewTypedUI is StreamUI &&
                        !viewTypedUI.isExpanded
                    ) {
                        viewTypedUI.isExpanded = true
                        indexOfTopicInsertion = index + 1
                    } else if (viewTypedUI.uid == uidOfStreamUI &&
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
                        }
                        clickedView.setBackgroundColor(
                            resources.getColor(
                                R.color.gray_primary_background,
                                context?.theme
                            )
                        )
                        animateExpandImageViewButton(expandImageView)
                    } else {
                        innerViewTypedList.addAll(indexOfTopicInsertion, topicUIList)
                        clickedView.setBackgroundColor(
                            resources.getColor(
                                R.color.gray_secondary_background,
                                context?.theme
                            )
                        )
                        animateExpandImageViewButton(expandImageView)

                    }
                } catch (e: IndexOutOfBoundsException) {
                    Toast.makeText(
                        this.context,
                        resources.getString(R.string.errorExpandStream),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                asyncAdapter.items.submitList(innerViewTypedList.map { it })
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
        innerViewTypedList = getStreamUIListFromFakeServer().toMutableList()
        asyncAdapter.items.submitList(getStreamUIListFromFakeServer())
    }

    private fun getStreamUIListFromFakeServer(): List<ViewTyped> {
        return fakeServer.subscribedStreamsWithUid.flatMap { (stream, uid) ->
            listOf(
                StreamUI(
                    stream,
                    uid
                )
            ) + listOf(StreamAndTopicSeparatorUI(uid = "STREAM_SEPARATOR_$uid"))
        }
    }

    private fun getTopicsUIListByStreamUid(streamUid: String): List<ViewTyped> {
        val listOfTopics = fakeServer.topicsByStreamUid
            .filter { (uid, _) ->
                uid == streamUid
            }.values.flatten()
        return listOfTopics.flatMap {
            listOf(TopicUI(name = it.name, uid = it.uid)) + listOf(
                StreamAndTopicSeparatorUI(
                    uid = "TOPIC_SEPARATOR_${it.uid}"
                )
            )
        }
    }

    private fun animateExpandImageViewButton(imageView: ImageView) {
        val anim = ObjectAnimator.ofFloat(imageView, "rotation", rotationAngle, rotationAngle + 180)
        anim.duration = 150
        anim.start()
        rotationAngle += 180
        rotationAngle %= 360
    }

    companion object {
        const val EXTRA_NAME_OF_STREAM = "EXTRA_NAME_OF_STREAM"
        const val EXTRA_NAME_OF_TOPIC = "EXTRA_NAME_OF_TOPIC"
    }


}