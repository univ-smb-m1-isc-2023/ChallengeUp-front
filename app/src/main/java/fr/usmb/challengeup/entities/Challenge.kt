package fr.usmb.challengeup.entities

enum class Periodicity {
    QUOTIDIEN,
    HEBDOMADAIRE,
    MENSUEL
}

// pour l'adapter de la RecyclerView, une liste de challenge (ou de challenge suggérés) mais il y a qu'un adapter
// pour DEUX RecyclerView
data class Challenge(
    val id: Int,
    val title: String,
    val tag: String,
    val periodicity: Periodicity,
    val description: String
)
