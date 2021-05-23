package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.MyApp
import com.turik2304.coursework.data.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.databinding.FragmentOwnProfileBinding
import com.turik2304.coursework.di.modules.PeopleModule
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersUiState
import com.turik2304.coursework.presentation.base.MviFragment
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.presentation.utils.SetStatusUtil.setColoredTextStatus
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Named

class OwnProfileFragment : MviFragment<UsersActions, UsersUiState>() {

    @field:[Inject Named(PeopleModule.OWN_PROFILE_STORE)]
    override lateinit var store: Store<UsersActions, UsersUiState>

    @Inject
    override lateinit var actions: PublishRelay<UsersActions>

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

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
        (activity?.application as MyApp).peopleComponent?.inject(this)
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
        renderLoading(state.isLoading)
        renderError(state.error)
        renderOwnProfile(state.data)
    }

    private fun renderLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.ownProfileShimmer.showShimmer(true)
        } else {
            binding.ownProfileShimmer.stopAndHideShimmer()
        }
    }

    private fun renderError(error: Throwable?) {
        error?.let {
            Error.showError(context, it)
        }
    }

    private fun renderOwnProfile(data: Any?) {
        if (data is GetOwnProfileResponse) {
            binding.tvUserNameProfileTab.text = data.name
            binding.tvStatusProfile.setColoredTextStatus(data.statusEnum)
            val avatarUrl = data.avatarUrl
            Glide.with(this).load(avatarUrl).into(binding.imUserAvatarProfileTab)
        }
    }
}