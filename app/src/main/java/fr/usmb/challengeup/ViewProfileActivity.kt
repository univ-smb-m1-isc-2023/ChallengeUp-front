package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.utils.SharedPreferencesManager

class ViewProfileActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        supportActionBar?.setBackgroundDrawable(null)
        val usernameToVisit = intent.getStringExtra("viewUser")
        if (usernameToVisit == null) {
            val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            user = sharedPreferencesManager.getUserFromSharedPrefs()!!
        } else {
            // évidemment à instancier avec les vraies informations du gars qu'on visite
            // mais pour l'instant, il n'y a qu'un User en base donc bon...
            user = User(-1, usernameToVisit, null, null)
        }

        title = "Profil de ${user.username}"

        val profileUsername = findViewById<TextView>(R.id.profileUsername)
        profileUsername.text = "${user.username} d'ID ${user.id}"
    }

    /**
     * Lorsqu'on appuie sur la flèche de retour
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}