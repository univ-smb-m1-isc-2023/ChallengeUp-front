package fr.usmb.challengeup.network

import fr.usmb.challengeup.entities.User

/**
 * Gestion de la connexion à l'application
 */
class ConnectionManager(
    private val username : String,
    private val password : String,
    private val stayConnected : Boolean
) {
    fun doConnection() : User? {
        val user : User = User(0, username, username)
        if (username.isNotEmpty() && password.isNotEmpty() && stayConnected){
            return user
        }
        return null
    }
}