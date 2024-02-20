package fr.usmb.challengeup.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Gère les fragments derrière les Tabs de la page d'accueil
 * https://developer.android.com/guide/navigation/navigation-swipe-view-2
 */
class ViewPagerAdapter(
    private val fragmentList: List<Fragment>,
    private val titleList: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getPageTitle(position: Int): String {
        return titleList[position]
    }
}