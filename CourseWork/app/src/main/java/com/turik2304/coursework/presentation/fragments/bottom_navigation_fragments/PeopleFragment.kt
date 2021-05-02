package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.shimmer.ShimmerFrameLayout
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.R
import com.turik2304.coursework.data.network.models.data.StatusEnum
import com.turik2304.coursework.databinding.FragmentPeopleBinding
import com.turik2304.coursework.domain.UsersMiddleware
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.GeneralActions
import com.turik2304.coursework.presentation.GeneralReducer
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.GeneralUiState
import com.turik2304.coursework.presentation.recycler_view.AsyncAdapter
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.holder_factories.MainHolderFactory
import com.turik2304.coursework.presentation.recycler_view.items.UserUI
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.Search
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PeopleFragment : MviFragment<GeneralActions, GeneralUiState>() {

    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>

    override val actions: PublishRelay<GeneralActions> = PublishRelay.create()
    override val store: Store<GeneralActions, GeneralUiState> = Store(
        reducer = GeneralReducer(),
        middlewares = listOf(UsersMiddleware()),
        initialState = GeneralUiState()
    )
    private val compositeDisposable = CompositeDisposable()

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clickListener = { clickedView: View ->
            val userShimmer = clickedView as ShimmerFrameLayout
            userShimmer.showShimmer(true)
            val positionOfClickedView =
                binding.recycleViewUsers.getChildAdapterPosition(clickedView)
            val clickedUserUI = asyncAdapter.items.currentList[positionOfClickedView] as UserUI
            loadProfileDetails(clickedUserUI)
            userShimmer.stopAndHideShimmer()
        }

        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()
        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        binding.recycleViewUsers.adapter = asyncAdapter

        compositeDisposable += store.wire()
        compositeDisposable += store.bind(this)
        actions.accept(GeneralActions.LoadItems)
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
        _binding = null
    }

    override fun render(state: GeneralUiState) {
        if (state.isLoading) {
            binding.usersShimmer.showShimmer(true)
        } else {
            binding.usersShimmer.stopAndHideShimmer()
        }
        if (state.error != null) {
            binding.usersShimmer.stopAndHideShimmer()
            Error.showError(context, state.error)
        }
        if (state.data != null) {
            val userList = state.data as List<ViewTyped>
            asyncAdapter.items.submitList(userList)
            Search.initSearch(
                editText = binding.edSearchUsers,
                recyclerView = binding.recycleViewUsers
            )
        }
    }
}


