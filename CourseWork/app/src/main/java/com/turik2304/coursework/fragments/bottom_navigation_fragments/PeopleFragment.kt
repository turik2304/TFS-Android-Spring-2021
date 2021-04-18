package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.util.Log
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
import com.turik2304.coursework.network.RetroClient
import com.turik2304.coursework.network.ZulipAPICallHandler
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.UserUI
import com.turik2304.coursework.stopAndHideShimmer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class PeopleFragment : Fragment() {

    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
    private val compositeDisposable = CompositeDisposable()

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

        val clickListener = { clickedView: View ->
            val userShimmer = clickedView as ShimmerFrameLayout
            userShimmer.showShimmer(true)
            val positionOfClickedView =
                recyclerViewUsers.getChildAdapterPosition(clickedView)
            val clickedUserUI = asyncAdapter.items.currentList[positionOfClickedView] as UserUI
            loadProfileDetails(clickedUserUI)
        }
        val editText = view.findViewById<EditText>(R.id.edSearchUsers)
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()

        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewUsers.adapter = asyncAdapter
        compositeDisposable.add(
            RetroClient.zulipApi.getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        asyncAdapter.items.submitList(
                            response.members.sortedBy { user -> user.userName }
                        )
                        innerViewTypedList = response.members
                        Search.initSearch(
                            editText,
                            innerViewTypedList,
                            asyncAdapter,
                            recyclerViewUsers
                        )
                        usersToolbarShimmer.stopAndHideShimmer()
                        response.members.forEachIndexed { index, user ->
                            if (!user.isBot) {
                                RetroClient.zulipApi.getUserPresence(user.email)
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ presenceResponse ->
                                        user.presence = presenceResponse.presence.aggregated.status
                                    },
                                        { onError ->
                                            Error.showError(
                                                context,
                                                onError
                                            )
                                            usersToolbarShimmer.stopAndHideShimmer()
                                        })
                            }
                        }
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        usersToolbarShimmer.stopAndHideShimmer()
                    })
        )
    }

    private fun startProfileDetailsFragment(userName: String, status: String, avatarUrl: String) {
        parentFragmentManager.beginTransaction()
            .add(
                R.id.fragmentContainer,
                ProfileDetailsFragment.newInstance(
                    userName,
                    status,
                    avatarUrl
                )
            )
            .addToBackStack(null)
            .commit()
    }

    private fun loadProfileDetails(
        userUI: UserUI,
    ) {
        val name = userUI.userName
        val status = userUI.presence
        val avatarUrl = userUI.avatarUrl
        startProfileDetailsFragment(name, status, avatarUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}

