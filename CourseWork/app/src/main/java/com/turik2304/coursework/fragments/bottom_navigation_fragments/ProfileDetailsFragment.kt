package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.turik2304.coursework.MainActivity
import com.turik2304.coursework.R

class ProfileDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userNameTextView = view.findViewById<TextView>(R.id.tvUserNameDetailsProfile)
        val statusTextView = view.findViewById<TextView>(R.id.tvStatusProfileDetails)
        val backImageView = view.findViewById<ImageView>(R.id.imBackProfileDetails)
        val avatar = view.findViewById<ImageView>(R.id.imUserAvatarProfileTab)

        backImageView.setOnClickListener { (context as MainActivity).onBackPressed() }

        userNameTextView.text = requireArguments().getString(ARG_USER_NAME, "none")
        statusTextView.text = requireArguments().getString(ARG_STATUS, "none")
        val avatarUrl = requireArguments().getString(ARG_AVATAR_URL, "")
        Glide.with(this).load(avatarUrl).into(avatar)
        SetStatusUtil.setColoredTextStatus(statusTextView)
    }

    companion object {
        private const val ARG_USER_NAME = "userName"
        private const val ARG_STATUS = "status"
        private const val ARG_AVATAR_URL = "avatarUrl"

        fun newInstance(
            userName: String,
            status: String,
            avatarUrl: String
        ): ProfileDetailsFragment {
            val fragment = ProfileDetailsFragment()
            val arguments = Bundle().apply {
                putString(ARG_USER_NAME, userName)
                putString(ARG_STATUS, status)
                putString(ARG_AVATAR_URL, avatarUrl)
            }
            fragment.arguments = arguments
            return fragment
        }
    }
}