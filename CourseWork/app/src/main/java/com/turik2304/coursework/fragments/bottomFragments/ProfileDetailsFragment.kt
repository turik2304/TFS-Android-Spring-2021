package com.turik2304.coursework.fragments.bottomFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
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
        val statusTextTextView = view.findViewById<TextView>(R.id.tvStatusTextProfileDetails)
        val statusTextView = view.findViewById<TextView>(R.id.tvStatusProfileDetails)

        userNameTextView.text = requireArguments().getString(ARG_USER_NAME, "none")
        statusTextTextView.text = requireArguments().getString(ARG_STATUS_TEXT, "none")
        statusTextView.text = requireArguments().getString(ARG_STATUS, "none")
    }

    companion object {

        private const val ARG_USER_NAME = "userName"
        private const val ARG_STATUS_TEXT = "statusText"
        private const val ARG_STATUS = "status"

        fun newInstance(userName: String, statusText: String, status: String): ProfileDetailsFragment {
            val fragment = ProfileDetailsFragment()
            val arguments = Bundle().apply {
                putString(ARG_USER_NAME, userName)
                putString(ARG_STATUS_TEXT, statusText)
                putString(ARG_STATUS, status)
            }
            fragment.arguments = arguments
            return fragment
        }
    }
}