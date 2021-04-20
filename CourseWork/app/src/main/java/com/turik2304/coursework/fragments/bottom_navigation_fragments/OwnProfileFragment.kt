package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.Error
import com.turik2304.coursework.R
import com.turik2304.coursework.network.ZulipRepository
import com.turik2304.coursework.stopAndHideShimmer
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

        disposableGetOwnProfile = ZulipRepository.getOwnProfile()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ nameAndPresenceResponse ->
                userNameTextView.text = nameAndPresenceResponse.first
                statusTextView.text = nameAndPresenceResponse.second
                SetStatusUtil.setColoredTextStatus(statusTextView)
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