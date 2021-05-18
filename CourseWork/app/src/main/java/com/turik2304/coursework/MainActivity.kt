package com.turik2304.coursework

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.turik2304.coursework.databinding.ActivityMainBinding
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.ChannelsFragment
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.OwnProfileFragment
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.PeopleFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var activeFragment: Fragment = ChannelsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, activeFragment)
                .commit()
        }

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navChannels -> {
                    (applicationContext as MyApp).clearPeopleComponent()
                    activeFragment = ChannelsFragment()
                }
                R.id.navPeople -> {
                    (applicationContext as MyApp).addPeopleComponent()
                    activeFragment = PeopleFragment()
                }
                R.id.navProfile -> {
                    (applicationContext as MyApp).addPeopleComponent()
                    activeFragment = OwnProfileFragment()
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, activeFragment)
                .setReorderingAllowed(true)
                .commit()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (applicationContext as MyApp).clearAllComponents()
    }
}