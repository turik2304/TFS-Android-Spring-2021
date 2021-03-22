package com.turik2304.coursework.fragments.viewPagerFragments

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.R
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.diff_utils.DiffCallbackStreamUI
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import java.lang.IndexOutOfBoundsException

class SubscribedFragment : Fragment() {

    private val fakeServer: ServerApi = FakeServerApi()
    private lateinit var innerViewTypedList: MutableList<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

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
            if (clickedView is LinearLayout &&
                clickedView.tag != resources.getString(R.string.tagTopicLinearLayout)
            ) {
                val uidOfStreamUI = clickedView.tag.toString()
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
                            innerViewTypedList.removeAt(indexOfTopicInsertion)
                            if (indexOfTopicInsertion == innerViewTypedList.size) break
                        }
                        animateExpandImageViewButton(expandImageView)
                    } else {
                        innerViewTypedList.addAll(indexOfTopicInsertion, topicUIList)
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
                val uidOfTopicUI = clickedView
                    .findViewById<TextView>(R.id.tvNameOfTopic).tag.toString()
                //some logic to start chat by topic ID
                val intent = Intent(context, ChatActivity::class.java)
                startActivity(intent)
            }
            return@clickListener
        }
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallbackStreamUI()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewSubscribedStreams.adapter = asyncAdapter
        innerViewTypedList = getStreamUIListFromFakeServer().toMutableList()
        asyncAdapter.items.submitList(getStreamUIListFromFakeServer())
    }

    private fun getStreamUIListFromFakeServer(): List<ViewTyped> {
        return fakeServer.subscribedStreamsWithUid.flatMap { (stream, uid) ->
            listOf(StreamUI(stream, uid))
        }
    }

    private fun getTopicsUIListByStreamUid(streamUid: String): List<ViewTyped> {
        val listOfTopics = fakeServer.topicsByStreamUid
            .filter { (uid, _) ->
                uid == streamUid
            }.values.flatten()
        return listOfTopics.map { TopicUI(name = it.name, uid = it.uid) }

    }

    private fun animateExpandImageViewButton(imageView: ImageView) {
        val anim = ObjectAnimator.ofFloat(imageView, "rotation", rotationAngle, rotationAngle + 180)
        anim.duration = 300
        anim.start()
        rotationAngle += 180
        rotationAngle %= 360
    }


}