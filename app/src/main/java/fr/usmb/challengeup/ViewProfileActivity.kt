package fr.usmb.challengeup

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONObject

class ViewProfileActivity : AppCompatActivity(), UserFeedbackInterface {
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

        user.username?.let {
            watchProfileRequest(it, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    showSnackbarMessage(profileUsername, "Succès de la requête !")
                }
                override fun onError() {
                    Snackbar.make(profileUsername, "Vous n'avez rien à faire ici", Snackbar.LENGTH_INDEFINITE)
                        .setBackgroundTint(Color.RED)
                        .setActionTextColor(Color.WHITE)
                        .setAction("RETOUR") { finish() }
                        .show()
                }
            })
        }
    }

    /**
     * Lorsqu'on appuie sur la flèche de retour
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun watchProfileRequest(username: String, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "${getString(R.string.server_domain)}/user/profile/$username"

        val request = JsonObjectRequest(url,
            { response: JSONObject -> callback.onSuccess(response.toString()) },
            { error ->
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
                    val code = networkResponse.statusCode
                    val body = String(networkResponse.data)
                    showToastMessage(applicationContext, "Erreur $code : $body")
                } else showToastMessage(applicationContext, error.message.toString())
                callback.onError()
            }
        )
        queue.add(request)
    }
}