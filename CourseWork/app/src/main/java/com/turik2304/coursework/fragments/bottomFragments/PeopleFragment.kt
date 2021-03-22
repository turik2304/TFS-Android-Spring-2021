package com.turik2304.coursework.fragments.bottomFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.R
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.diff_utils.DiffCallbackStreamUI
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.UserUI

class PeopleFragment : Fragment() {

    private val fakeServer: ServerApi = FakeServerApi()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clickListener = { clickedView: View ->
            if (clickedView is LinearLayout) {

                val uidOfClickedUser = clickedView.tag.toString()
                val profileDetailsFragment = fakeServer.getProfileDetailsById(uidOfClickedUser)
                var userName = ""
                var statusText = ""
                var status = ""

                profileDetailsFragment.forEach {  map ->
                    when (map.key) {
                        "userName" -> userName = map.value
                        "statusText" -> statusText =  map.value
                        "status" -> status = map.value
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, ProfileDetailsFragment.newInstance(userName, statusText, status))
                    .addToBackStack(null)
                    .commit()
            }
        }

        val recyclerViewUsers = view.findViewById<RecyclerView>(R.id.recycleViewUsers)
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallbackStreamUI()

        val asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewUsers.adapter = asyncAdapter
        asyncAdapter.items.submitList(getUserUIListFromFakeServer())


    }

    private fun getUserUIListFromFakeServer(): List<ViewTyped> {
        return fakeServer.userList.flatMap { (uid, userName, email) ->
            listOf(UserUI(userName, email, uid))
        }
    }
}