package com.turik2304.coursework

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.turik2304.coursework.databinding.ActivityMainBinding
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.OwnProfileFragment
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.PeopleFragment
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.StreamsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var activeFragment: Fragment = StreamsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val app = application as MyApp

        if (savedInstanceState == null) {
            app.addStreamsComponent()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, activeFragment)
                .commit()
        }

        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navChannels -> {
                    app.clearPeopleComponent()
                    app.addStreamsComponent()
                    activeFragment = StreamsFragment()
                }
                R.id.navPeople -> {
                    app.clearStreamsComponent()
                    app.addPeopleComponent()
                    activeFragment = PeopleFragment()
                }
                R.id.navProfile -> {
                    app.clearStreamsComponent()
                    app.addPeopleComponent()
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

//    override fun onDestroy() {
//        super.onDestroy()
//        (applicationContext as MyApp).clearAllComponents()
//    }
}