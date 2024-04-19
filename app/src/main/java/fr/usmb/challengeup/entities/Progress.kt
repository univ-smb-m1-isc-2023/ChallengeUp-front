package fr.usmb.challengeup.entities

import com.google.gson.Gson

data class Progress(
    val id: Long,
    val date: String,
    val challenge: Challenge,
    val user: User,
    val completed: Boolean
) {
    /**
     * Renvoyer un challenge jsonifié pour le passer au serveur
     */
    fun toJSON(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    /**
     * Convertit un JSON en Challenge
     */
    fun fromJSON(json: String): Progress {
        val gson = Gson()
        return gson.fromJson(json, Progress::class.java)
    }
}