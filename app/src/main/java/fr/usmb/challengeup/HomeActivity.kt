package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import fr.usmb.challengeup.adapter.ViewPagerAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // cacher le titre de l'activité par défaut
        supportActionBar?.hide()

        // utiliser ViewPager2 https://developer.android.com/reference/com/google/android/material/tabs/TabLayout
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.homeTabLayout)

        val fragmentList : List<Fragment> = listOf(DashboardFragment(), SuggestionsFragment())
        val adapter = ViewPagerAdapter(
            fragmentList,
            listOf("Tableau de bord", "Suggestions"),
            supportFragmentManager,
            lifecycle
        )
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) {tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.homeNavigationDrawer)

        topAppBar.setOnClickListener {
            drawerLayout.open()
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.close()
            true
        }
    }
}