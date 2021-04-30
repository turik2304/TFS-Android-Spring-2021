package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.Error
import com.turik2304.coursework.R
import com.turik2304.coursework.Search
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.network.models.data.StatusEnum
import com.turik2304.coursework.presentation.UsersStore
import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.base.MviView
import com.turik2304.coursework.presentation.base.UiState
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.UserUI
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PeopleFragment : Fragment(),
    MviView<Action, UiState> {

    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
    private lateinit var usersToolbarShimmer: ShimmerFrameLayout
    private lateinit var searchUsersEditText: EditText
    private lateinit var usersRecyclerView: RecyclerView

    override val actions: PublishRelay<Action> = PublishRelay.create()
    private val compositeDisposable = CompositeDisposable()
    private val usersStore = UsersStore()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usersToolbarShimmer = view.findViewById(R.id.usersShimmer)
        searchUsersEditText = view.findViewById(R.id.edSearchUsers)
        usersRecyclerView = view.findViewById(R.id.recycleViewUsers)

        val clickListener = { clickedView: View ->
            val userShimmer = clickedView as ShimmerFrameLayout
            userShimmer.showShimmer(true)
            val positionOfClickedView =
                usersRecyclerView.getChildAdapterPosition(clickedView)
            val clickedUserUI = asyncAdapter.items.currentList[positionOfClickedView] as UserUI
            loadProfileDetails(clickedUserUI)
            userShimmer.stopAndHideShimmer()
        }

        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        usersRecyclerView.adapter = asyncAdapter

        usersStore.wire()
        compositeDisposable += usersStore.bind(this)
        actions.accept(Action.LoadItems)
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

    override fun render(state: UiState) {
        if (state.isLoading) {
            usersToolbarShimmer.showShimmer(true)
        } else {
            usersToolbarShimmer.stopAndHideShimmer()
        }
        if (state.error != null) {
            usersToolbarShimmer.stopAndHideShimmer()
            Error.showError(context, state.error)
        }
        if (state.data != null) {
            val userList = state.data as List<ViewTyped>
            asyncAdapter.items.submitList(userList)
            Search.initSearch(
                editText = searchUsersEditText,
                recyclerView = usersRecyclerView
            )
        }
    }
}


