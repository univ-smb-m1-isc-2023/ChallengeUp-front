package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // utiliser ViewPager2 https://developer.android.com/reference/com/google/android/material/tabs/TabLayout
        val tabLayout = findViewById<TabLayout>(R.id.homeTabLayout)

        //tabLayout.setupWithViewPager()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // Handle tab select
                val position = tab?.position
                Toast
                    .makeText(applicationContext,"Tab at position $position is selected.", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
                val position = tab?.position
                Toast
                    .makeText(applicationContext,"Tab at position $position is reselected.",Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
                val position = tab?.position
                Toast
                    .makeText(applicationContext,"Tab at position $position is unselected.",Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}