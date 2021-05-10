package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.R
import com.turik2304.coursework.data.network.models.data.StatusEnum
import com.turik2304.coursework.databinding.FragmentPeopleBinding
import com.turik2304.coursework.domain.UsersMiddleware
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersReducer
import com.turik2304.coursework.presentation.UsersUiState
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.base.Recycler
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.holder_factories.MainHolderFactory
import com.turik2304.coursework.presentation.recycler_view.items.UserUI
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.Search
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PeopleFragment : MviFragment<UsersActions, UsersUiState>() {

    private lateinit var recycler: Recycler<ViewTyped>

    override val actions: PublishRelay<UsersActions> = PublishRelay.create()
    override val store: Store<UsersActions, UsersUiState> = Store(
        reducer = UsersReducer(),
        middlewares = listOf(UsersMiddleware()),
        initialState = UsersUiState()
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
        initRecycler()
        initRecyclerClicks()
        compositeDisposable += store.wire()
        compositeDisposable += store.bind(this)
        actions.accept(UsersActions.LoadUsers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    override fun render(state: UsersUiState) {
        if (state.isLoading) {
            binding.usersShimmer.showShimmer(true)
        } else {
            binding.usersShimmer.stopAndHideShimmer()
        }
        state.error?.let { Error.showError(context, state.error) }
        state.data?.let { users ->
            recycler.setItems(users as List<UserUI>)
            Search.initSearch(
                editText = binding.edSearchUsers,
                recyclerView = binding.recycleViewUsers
            )
        }
        state.userInfo?.let { user ->
            startProfileDetailsFragment(
                userName = user.userName,
                status = user.presence,
                avatarUrl = user.avatarUrl
            )
        }
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

    private fun initRecycler() {
        recycler = Recycler(
            recyclerView = binding.recycleViewUsers,
            diffCallback = DiffCallback<ViewTyped>(),
            holderFactory = MainHolderFactory()
        )
    }

    private fun initRecyclerClicks() {
        compositeDisposable += recycler.clickedItem<UserUI>(R.layout.item_user)
            .map { UsersActions.OpenUserInfo(user = it) }
            .subscribe { actions.accept(it) }
    }
}


