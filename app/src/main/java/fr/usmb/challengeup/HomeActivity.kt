package fr.usmb.challengeup

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import fr.usmb.challengeup.adapter.ViewPagerAdapter
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.ConnectionManager
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface

class HomeActivity : AppCompatActivity(), UserFeedbackInterface {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // cacher le titre de l'activité par défaut
        supportActionBar?.hide()

        // utiliser ViewPager2 https://developer.android.com/reference/com/google/android/material/tabs/TabLayout
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.homeTabLayout)
        user = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    intent.getParcelableExtra("user", User::class.java)!!
                else intent.getParcelableExtra<User>("user")!!

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
            when(menuItem.itemId){
                R.id.disconnectMe -> {
                    val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
                    sharedPreferencesManager.saveStayConnectedToSharedPrefs(false)
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
                R.id.watchProfileDrawer -> {
                    showDialog()
                }
                R.id.setMyProfilePublic -> {
                    val cm = ConnectionManager("", "", false)
                    cm.tryConnexion(applicationContext)
                }
                else -> false
            }
            true
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.account -> {
                    intent = Intent(applicationContext, ViewProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun showDialog() {
        val textInputLayout = TextInputLayout(this).apply {
            hint = resources.getString(R.string.username2)
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            prefixText = "@"
        }

        val editText = TextInputEditText(textInputLayout.context)
        textInputLayout.addView(editText)

        MaterialAlertDialogBuilder(this)
            .setTitle("Consulter un profil")
            .setMessage("Rentrer le nom d'utilisateur du profil de la personne que vous souhaitez consulter.\n")
            .setView(textInputLayout)
            .setNeutralButton("Annuler") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Consulter") { dialog, _ ->
                val userToVisit = editText.text.toString()
                intent = Intent(applicationContext, ViewProfileActivity::class.java)
                intent.putExtra("viewUser", userToVisit)
                startActivity(intent)
                dialog.dismiss()
            }
            .setIcon(R.drawable.ic_account_circle)
            .show()
    }

}