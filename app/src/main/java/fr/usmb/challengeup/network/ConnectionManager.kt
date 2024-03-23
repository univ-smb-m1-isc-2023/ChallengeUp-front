package fr.usmb.challengeup.network

import android.content.Context
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.utils.UserFeedbackInterface

/**
 * Gestion de la connexion à l'application
 */
class ConnectionManager(
    private val username : String,
    private val password : String,
    private val stayConnected : Boolean
): UserFeedbackInterface {
    fun doConnection() : User? {
        val user : User = User(0, username, username)
        if (username.isNotEmpty() && password.isNotEmpty() && stayConnected){
            return user
        }
        return null
    }

    fun tryConnexion(ctx: Context) {
        val queue = Volley.newRequestQueue(ctx)
        val url = "https://challenge-up.oups.net/hello?name=du%20serveur"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                showToastMessage(ctx, response.toString(), Toast.LENGTH_SHORT)
            },
            { showToastMessage(ctx, "Impossible de se connecter au serveur...", Toast.LENGTH_SHORT) })

        // Ajouter la requête à la RequestQueue.
        queue.add(stringRequest)
    }
}