package com.turik2304.coursework.fragments.viewPagerFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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

class AllStreamsFragment : Fragment() {

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
        val diffCallBack = DiffCallbackStreamUI()

        val asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewAllStreams.adapter = asyncAdapter
        asyncAdapter.items.submitList(getStreamsAndTopicsItemsFromFakeServer())
    }

    private fun getStreamsAndTopicsItemsFromFakeServer(): List<ViewTyped> {
        return fakeServer.allStreams.map { StreamUI(it) }

    }

}