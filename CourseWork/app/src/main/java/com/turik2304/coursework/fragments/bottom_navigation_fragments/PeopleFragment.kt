package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.Error
import com.turik2304.coursework.R
import com.turik2304.coursework.Search
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.UserUI
import com.turik2304.coursework.stopAndHideShimmer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

class PeopleFragment : Fragment() {

    private val api: ServerApi = FakeServerApi()
    private lateinit var innerViewTypedList: List<ViewTyped>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usersToolbarShimmer = view.findViewById<ShimmerFrameLayout>(R.id.usersShimmer)
        usersToolbarShimmer.startShimmer()

        val recyclerViewUsers = view.findViewById<RecyclerView>(R.id.recycleViewUsers)

        val clickListener = clickListener@{ clickedView: View ->
            val userShimmer = clickedView as ShimmerFrameLayout
            userShimmer.showShimmer(true)
            val positionOfClickedView =
                recyclerViewUsers.getChildAdapterPosition(clickedView)
            val clickedItem = innerViewTypedList[positionOfClickedView]
            val uidOfClickedUser = clickedItem.uid
            val userUI = innerViewTypedList.find { it.uid == uidOfClickedUser } as UserUI
            val emailOfUser = userUI.email
            val nameOfUser = userUI.userName

            api.getProfileDetailsById(emailOfUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { status ->
                        startProfileDetailsFragment(nameOfUser, status, status)
                        userShimmer.stopAndHideShimmer()
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        userShimmer.stopAndHideShimmer()
                    })
            return@clickListener
        }
        val editText = view.findViewById<EditText>(R.id.edSearchUsers)
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()

        val asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewUsers.adapter = asyncAdapter

        api.getUserUIListFromServer()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userList ->
                    asyncAdapter.items.submitList(userList)
                    innerViewTypedList = userList
                        Search.initSearch(
                        editText,
                        innerViewTypedList,
                        asyncAdapter,
                        recyclerViewUsers
                    )
                    usersToolbarShimmer.stopAndHideShimmer()
                },
                { onError ->
                    Error.showError(
                        context,
                        onError
                    )
                    usersToolbarShimmer.stopAndHideShimmer()
                })
    }

    private fun startProfileDetailsFragment(userName: String, statusText: String, status: String) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ProfileDetailsFragment.newInstance(
                    userName,
                    statusText,
                    status
                )
            )
            .addToBackStack(null)
            .commit()
    }

}

