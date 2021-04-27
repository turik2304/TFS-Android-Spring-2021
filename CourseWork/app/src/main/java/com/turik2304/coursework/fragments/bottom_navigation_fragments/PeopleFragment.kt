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
import com.turik2304.coursework.network.ZulipRepository
import com.turik2304.coursework.network.ZulipRepository.db
import com.turik2304.coursework.network.models.data.StatusEnum
import com.turik2304.coursework.network.models.response.ResponseType
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.UserUI
import com.turik2304.coursework.stopAndHideShimmer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
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
            userShimmer.stopAndHideShimmer()
        }
        val editText = view.findViewById<EditText>(R.id.edSearchUsers)
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewUsers.adapter = asyncAdapter

        compositeDisposable.add(
            ZulipRepository.getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { userListAndResponseType ->
                        val userList = userListAndResponseType.first
                        val responseType = userListAndResponseType.second
                        when (responseType) {
                            ResponseType.FROM_DB -> asyncAdapter.items.submitList(userList) {
                                Search.initSearch(
                                    editText,
                                    userList,
                                    asyncAdapter,
                                    recyclerViewUsers
                                )
                            }
                            ResponseType.FROM_NETWORK -> {
                                val userListWithPresences = mutableListOf<UserUI>()
                                userList.forEach { user ->
                                    ZulipRepository.updateUserPresence(user)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({ updatedUser ->
                                            userListWithPresences.add(updatedUser)
                                            if (userListWithPresences.size == userList.size) {
                                                asyncAdapter.items.submitList(
                                                    userListWithPresences.sortedBy { it.userName }
                                                ) {
                                                    usersToolbarShimmer.stopAndHideShimmer()
                                                    Search.initSearch(
                                                        editText,
                                                        userListWithPresences,
                                                        asyncAdapter,
                                                        recyclerViewUsers
                                                    )
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
                                }
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

    private fun startProfileDetailsFragment(
        userName: String,
        status: StatusEnum,
        avatarUrl: String
    ) {
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

