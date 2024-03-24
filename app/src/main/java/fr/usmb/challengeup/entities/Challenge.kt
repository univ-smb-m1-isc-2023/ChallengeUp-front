package fr.usmb.challengeup.entities

import com.google.gson.Gson


enum class Periodicity {
    QUOTIDIEN,
    HEBDOMADAIRE,
    MENSUEL
}

// pour l'adapter de la RecyclerView, une liste de challenge (ou de challenge suggérés) mais il y a qu'un adapter
// pour DEUX RecyclerView
data class Challenge(
    val id: Long?,
    val title: String,
    val tag: String,
    val periodicity: Periodicity,
    val description: String
){

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
    fun fromJSON(json: String): Challenge {
        val gson = Gson()
        return gson.fromJson(json, Challenge::class.java)
    }
}
