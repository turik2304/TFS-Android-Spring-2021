package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.data.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.databinding.FragmentOwnProfileBinding
import com.turik2304.coursework.domain.OwnProfileMiddleware
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersReducer
import com.turik2304.coursework.presentation.UsersUiState
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.SetStatusUtil.setColoredTextStatus
import io.reactivex.rxjava3.disposables.CompositeDisposable

class OwnProfileFragment : MviFragment<UsersActions, UsersUiState>() {

    override val store: Store<UsersActions, UsersUiState> = Store(
        reducer = UsersReducer(),
        middlewares = listOf(OwnProfileMiddleware()),
        initialState = UsersUiState()
    )
    override val actions: PublishRelay<UsersActions> = PublishRelay.create()

    private val compositeDisposable = CompositeDisposable()

    private var _binding: FragmentOwnProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOwnProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            binding.ownProfileShimmer.showShimmer(true)
        } else {
            binding.ownProfileShimmer.stopAndHideShimmer()
        }
        if (state.error != null) {
            binding.ownProfileShimmer.showShimmer(true)
            Error.showError(context, state.error)
        }
        if (state.data != null) {
            val ownProfileInfo = state.data as GetOwnProfileResponse
            binding.tvUserNameProfileTab.text = ownProfileInfo.name
            binding.tvStatusProfile.setColoredTextStatus(ownProfileInfo.statusEnum)
            val avatarUrl = ownProfileInfo.avatarUrl
            Glide.with(this).load(avatarUrl).into(binding.imUserAvatarProfileTab)
        }
    }
}