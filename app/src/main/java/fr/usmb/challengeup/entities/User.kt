package fr.usmb.challengeup.entities

data class User(
    val id: Long,
    val username: String,
    val email: String
) : java.io.Serializable
