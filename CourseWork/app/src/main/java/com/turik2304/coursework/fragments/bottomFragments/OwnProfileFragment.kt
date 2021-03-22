package com.turik2304.coursework.fragments.bottomFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.turik2304.coursework.R

class OwnProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_own_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName = view.findViewById<TextView>(R.id.tvUserNameProfileTab)
        val statusText = view.findViewById<TextView>(R.id.tvStatusTextProfile)
        val status = view.findViewById<TextView>(R.id.tvStatusProfile)

        userName.text = "Artur Sibagatullin"
        statusText.text = "In a meeting"
        status.text = "online"

    }
}