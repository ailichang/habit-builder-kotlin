package com.habitbuilder.settings

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.habitbuilder.R
import com.habitbuilder.settings.faq.FaqFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationBarView

class SettingsFragment: PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        val appBarLayout = requireActivity().findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.setExpanded(true, false)
        val collapsingToolbarLayout =
            requireActivity().findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        collapsingToolbarLayout.visibility = View.VISIBLE
        collapsingToolbarLayout.title = requireContext().getString(R.string.settings_toolbar_title)
        val materialToolbar = requireActivity().findViewById<MaterialToolbar>(R.id.tool_bar)
        materialToolbar.navigationIcon = null
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        val bottomNavigationView =
            requireActivity().findViewById<NavigationBarView>(R.id.bottom_navigation)
        bottomNavigationView.visibility = View.VISIBLE

    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        setFaqPreference()
        setVersionPreference()
    }

    private fun setFaqPreference() {
        val faqPreference = findPreference<Preference>("faq")
        faqPreference?.onPreferenceClickListener =
            Preference.OnPreferenceClickListener {
                val fragmentManager = parentFragmentManager
                val transaction =
                    fragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.enter_anim,
                    R.anim.exit_anim
                )
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                transaction.replace(
                    R.id.nav_host_fragment,
                    FaqFragment(),
                    FaqFragment::class.qualifiedName.toString()
                )
                transaction.addToBackStack(null)
                transaction.commit()
                true
            }
    }

    private fun setVersionPreference() {
        val versionPreference = findPreference<Preference>("app_version")
        val packageManager = requireContext().packageManager
        val packageName = requireContext().packageName
        val packageInfo: PackageInfo
        try {
            packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                packageManager.getPackageInfo(packageName, 0)
            }
            val versionName = packageInfo.versionName
            versionPreference?.summary = versionName
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException(e)
        }
    }
}