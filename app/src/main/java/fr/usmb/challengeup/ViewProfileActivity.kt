package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import fr.usmb.challengeup.entities.User

class ViewProfileActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        supportActionBar?.setBackgroundDrawable(null)
        user = intent.getParcelableExtra("user")!!
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