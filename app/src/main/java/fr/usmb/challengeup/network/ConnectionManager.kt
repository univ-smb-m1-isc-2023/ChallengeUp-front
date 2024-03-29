package fr.usmb.challengeup.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.utils.UserFeedbackInterface
import com.google.gson.JsonObject

/**
 * Gestion de la connexion à l'application
 */
class ConnectionManager(
    private val username : String,
    private val password : String,
    private val stayConnected : Boolean
): UserFeedbackInterface {

    private val URL_SERVER = "https://challenge-up.oups.net"

    fun doConnection() : User? {
        val user : User = User(0, username, username, null)
        if (username.isNotEmpty() && password.isNotEmpty() && stayConnected){
            return user
        }
        return null
    }

    fun tryConnexion(ctx: Context) {
        val queue = Volley.newRequestQueue(ctx)
        val url = "$URL_SERVER/hello?name=du%20serveur"

        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                showToastMessage(ctx, response.toString())
            },
            { showToastMessage(ctx, "Impossible de se connecter au serveur...") })

        // Ajouter la requête à la RequestQueue.
        queue.add(stringRequest)
    }
}