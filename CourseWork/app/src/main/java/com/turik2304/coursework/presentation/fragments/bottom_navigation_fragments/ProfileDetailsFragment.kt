package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.turik2304.coursework.MainActivity
import com.turik2304.coursework.data.network.models.data.StatusEnum
import com.turik2304.coursework.databinding.FragmentProfileDetailsBinding
import com.turik2304.coursework.presentation.utils.SetStatusUtil.setColoredTextStatus

class ProfileDetailsFragment : Fragment() {

    private var _binding: FragmentProfileDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imBackProfileDetails.setOnClickListener { (context as MainActivity).onBackPressed() }

        binding.tvUserNameDetailsProfile.text = requireArguments().getString(ARG_USER_NAME, "none")
        val statusEnum = requireArguments().getSerializable(ARG_STATUS) as StatusEnum
        binding.tvStatusProfileDetails.text = statusEnum.status
        binding.tvStatusProfileDetails.setColoredTextStatus(statusEnum)
        val avatarUrl = requireArguments().getString(ARG_AVATAR_URL, "")
        Glide.with(this).load(avatarUrl).into(binding.imUserAvatarProfileTab)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USER_NAME = "userName"
        private const val ARG_STATUS = "status"
        private const val ARG_AVATAR_URL = "avatarUrl"

        fun newInstance(
            userName: String,
            status: StatusEnum,
            avatarUrl: String
        ): ProfileDetailsFragment {
            val fragment = ProfileDetailsFragment()
            val arguments = Bundle().apply {
                putString(ARG_USER_NAME, userName)
                putSerializable(ARG_STATUS, status)
                putString(ARG_AVATAR_URL, avatarUrl)
            }
            fragment.arguments = arguments
            return fragment
        }
    }
}