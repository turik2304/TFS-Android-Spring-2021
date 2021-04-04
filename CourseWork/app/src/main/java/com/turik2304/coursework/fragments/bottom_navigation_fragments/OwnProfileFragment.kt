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
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.stopAndHideShimmer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable

class OwnProfileFragment : Fragment() {

    private val api: ServerApi = FakeServerApi()
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
        val userName = view.findViewById<TextView>(R.id.tvUserNameProfileTab)
        val statusText = view.findViewById<TextView>(R.id.tvStatusTextProfile)
        val status = view.findViewById<TextView>(R.id.tvStatusProfile)

        disposableGetOwnProfile = api.getOwnProfile(requireActivity())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    val name = response["userName"]
                    userName.text = name
                    ownProfileShimmer.stopAndHideShimmer()
                },
                { onError ->
                    Error.showError(
                        context,
                        onError
                    )
                    ownProfileShimmer.stopAndHideShimmer()
                })

        statusText.text = "In a meeting"
        status.text = "online"
        status.setTextColor(resources.getColor(R.color.green_status_online, context?.theme))
    }

    override fun onStop() {
        super.onStop()
        disposableGetOwnProfile.dispose()
    }
}