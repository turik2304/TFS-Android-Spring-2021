package com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.turik2304.coursework.R
import com.turik2304.coursework.databinding.FragmentChannelsBinding
import com.turik2304.coursework.presentation.fragments.fragment_state_adapter.StreamsPagerAdapter
import com.turik2304.coursework.presentation.fragments.view_pager_fragments.AllStreamsFragment
import com.turik2304.coursework.presentation.fragments.view_pager_fragments.SubscribedFragment

class ChannelsFragment : Fragment() {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabs: List<String> = listOf(
            resources.getString(R.string.tab_subscribed_streams),
            resources.getString(R.string.tab_all_streams)
        )
        val pagerAdapter = StreamsPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.fragmentViewPager.adapter = pagerAdapter
        pagerAdapter.updatePages(listOf(SubscribedFragment(), AllStreamsFragment()))
        TabLayoutMediator(binding.tabLayout, binding.fragmentViewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}