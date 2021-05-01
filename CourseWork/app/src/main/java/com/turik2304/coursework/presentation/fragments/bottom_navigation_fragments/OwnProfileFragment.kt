package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.presentation.utils.Error
import com.turik2304.coursework.R
import com.turik2304.coursework.extensions.stopAndHideShimmer
import com.turik2304.coursework.presentation.utils.SetStatusUtil.setColoredTextStatus
import com.turik2304.coursework.data.repository.ZulipRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable

class OwnProfileFragment : Fragment() {

    private lateinit var disposableGetOwnProfile: Disposable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_own_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ownProfileShimmer = view as ShimmerFrameLayout
        ownProfileShimmer.startShimmer()
        val userNameTextView = view.findViewById<TextView>(R.id.tvUserNameProfileTab)
        val statusTextView = view.findViewById<TextView>(R.id.tvStatusProfile)
        val avatar = view.findViewById<ImageView>(R.id.imUserAvatarProfileTab)

        disposableGetOwnProfile = ZulipRepository.getOwnProfile()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ ownProfileResponse ->
                userNameTextView.text = ownProfileResponse.name
                statusTextView.text = ownProfileResponse.statusEnum.status
                val avatarUrl = ownProfileResponse.avatarUrl
                Glide.with(this).load(avatarUrl).into(avatar)
                statusTextView.setColoredTextStatus(ownProfileResponse.statusEnum)
                ownProfileShimmer.stopAndHideShimmer()
            },
                { onError ->
                    Error.showError(
                        context,
                        onError
                    )
                    ownProfileShimmer.stopAndHideShimmer()
                })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposableGetOwnProfile.dispose()
    }
}