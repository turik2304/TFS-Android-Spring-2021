package com.turik2304.coursework.fragments.bottomFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.turik2304.coursework.R
import com.turik2304.coursework.fragments.adapter.StreamsPagerAdapter
import com.turik2304.coursework.fragments.viewPagerFragments.AllStreamsFragment
import com.turik2304.coursework.fragments.viewPagerFragments.SubscribedFragment

class ChannelsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.fragmentViewPager)

        val tabs: List<String> = listOf("Subscribed", "All streams")
        val pagerAdapter = StreamsPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager.adapter = pagerAdapter
        pagerAdapter.updatePages(listOf(SubscribedFragment(), AllStreamsFragment()))
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()

    }


}