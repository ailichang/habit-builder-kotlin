package com.habitbuilder

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.habitbuilder.achievement.AchievementFragment
import com.habitbuilder.habit.HabitFragment
import com.habitbuilder.mission.MissionFragment
import com.habitbuilder.settings.SettingsFragment
import com.habitbuilder.tracker.TrackerFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val materialToolbar = findViewById<MaterialToolbar>(R.id.tool_bar)
        setSupportActionBar(materialToolbar)
        val bottomNavigationView = findViewById<NavigationBarView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_mission
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            var fragment: Fragment? = null
            var tag = ""
            when (item.itemId) {
                R.id.nav_habit -> {
                    fragment = HabitFragment()
                    tag = HabitFragment::class.qualifiedName.toString()
                }
                R.id.nav_mission -> {
                    fragment = MissionFragment()
                    tag = MissionFragment::class.qualifiedName.toString()
                }
                R.id.nav_achievement -> {
                    fragment = AchievementFragment()
                    tag = AchievementFragment::class.qualifiedName.toString()
                }
                R.id.nav_tracker -> {
                    fragment = TrackerFragment()
                    tag = TrackerFragment::class.qualifiedName.toString()
                }
                R.id.nav_settings -> {
                    fragment = SettingsFragment()
                    tag = SettingsFragment::class.qualifiedName.toString()
                }
            }
            if (fragment != null) {
                val fragmentManager = supportFragmentManager
                val transaction =
                    fragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.enter_anim,
                    R.anim.exit_anim
                )
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                transaction.replace(R.id.nav_host_fragment, fragment, tag)
                transaction.commit()
            }
            true
        }
    }
}