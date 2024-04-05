package fr.usmb.challengeup

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONObject

/**
 * Activité de connexion
 */
class MainActivity : AppCompatActivity(), UserFeedbackInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Connexion"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val loginButton = findViewById<Button>(R.id.login)
        val logWithSocialLoginButton = findViewById<Button>(R.id.logWithSocialLogin)
        val joinButton = findViewById<ExtendedFloatingActionButton>(R.id.joinButton)
        val username = findViewById<TextInputEditText>(R.id.usernameValue)
        val password = findViewById<TextInputEditText>(R.id.passwordValue)
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)

        // le bouton "Rester connecté" doit être coché par défaut
        stayConnectedSwitch.isChecked = true
        //stayConnectedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->  }

        loginButton.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                val user = User( 0, username, null, password)
                connectionRequest(user, object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        showToastMessage(applicationContext, "Connexion réussie")
                        connectionGranted(user)
                    }
                    override fun onError() {
                        showSnackbarMessage(loginButton, "Mauvais nom d'utilisateur ou mot de passe")
                    }
                })
            } else {
                showSnackbarMessage(loginButton, "Il manque des informations...")
            }
        }

        logWithSocialLoginButton.setOnClickListener {
            connectionGranted(User(0, "Jean-Eudes", "jean-eudes@mail.fr", null))
        }

        joinButton.setOnClickListener {
            intent = Intent(applicationContext, NewAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun connectionRequest(user: User, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "${getString(R.string.server_domain)}/auth/login"

        val jsonBody = JSONObject(user.toJSON())
        jsonBody.remove("id")

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response -> callback.onSuccess(response.toString())},
            { callback.onError() }
        )

        queue.add(request)
    }

    /**
     * Connexion réussie et passage à l'accueil de l'application
     */
    private fun connectionGranted(user: User?){
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)

        // Si l'utilsateur à cliquer sur "Rester connécté", on l'enregistre dans la mémoire du tél,
        // sinon, on le supprime
        if (stayConnectedSwitch.isChecked) user?.let { saveUserToSharedPrefs(it) }
        else saveUserToSharedPrefs(null)

        intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
        // une fois connecté, on n'a plus besoin de retourner sur l'activité de connexion donc on la détruit
        finish()
    }

    private fun getUserFromSharedPrefs(): User? {
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(getString(R.string.preference_user_key), null)
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }

    private fun saveUserToSharedPrefs(user: User?) {
        val sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val json = user?.toJSON()
        editor.putString(getString(R.string.preference_user_key), json)
        editor.apply()
    }
}