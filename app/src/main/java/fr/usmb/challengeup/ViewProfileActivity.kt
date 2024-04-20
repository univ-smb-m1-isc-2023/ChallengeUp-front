package fr.usmb.challengeup

import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONObject
import kotlin.random.Random

class ViewProfileActivity : AppCompatActivity(), UserFeedbackInterface {
    private lateinit var user: User
    private var regularity: Double = 0.0
    private var email: String = ""
    private var usernameToVisit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        supportActionBar?.setBackgroundDrawable(null)
        usernameToVisit = intent.getStringExtra("viewUser")
        if (usernameToVisit == null) {
            val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            user = sharedPreferencesManager.getUserFromSharedPrefs()!!
        } else {
            // évidemment à instancier avec les vraies informations du gars qu'on visite
            // mais pour l'instant, il n'y a qu'un User en base donc bon...
            user = User(-1, usernameToVisit, null, null)
        }

        val profileUsername = findViewById<TextView>(R.id.profileUsername)
        user.username?.let {
            watchProfileRequest(it, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    initActivity(JSONObject(result))
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
        var url = "${getString(R.string.server_domain)}/user/profile/$username"
        if (usernameToVisit == null)
            url = "${getString(R.string.server_domain)}/user/${user.id}"

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

    private fun initActivity(jsonUser: JSONObject) {
        regularity = jsonUser.getDouble("regularity")
        email = jsonUser.getString("email")
        title = "Profil de ${user.username}"

        val profileUsername = findViewById<TextView>(R.id.profileUsername)
        if(user.id > 0) profileUsername.text = "${user.username} #${user.id}"
        else profileUsername.text = user.username

        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        profileEmail.text = email
        val profileRegularity = findViewById<TextView>(R.id.profileRegularityValue)
        profileRegularity.text = "${getString(R.string.regularity)} : ${regularity.toInt()} %"

        val progressIndicator = findViewById<CircularProgressIndicator>(R.id.profileProgressRegularity)
        progressIndicator.trackColor = Color.LTGRAY
        val progessAnimatorValue = regularity.toInt()

        if (progessAnimatorValue < 50) progressIndicator.setIndicatorColor(Color.RED)
        else progressIndicator.setIndicatorColor(Color.GREEN)

        val progressAnimator = ValueAnimator.ofInt(0, progessAnimatorValue).apply {
            duration = 1500 // Durée de l'animation en millisecondes
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressIndicator.setProgressCompat(progress, false)
            }
        }
        progressAnimator.start()
    }
}