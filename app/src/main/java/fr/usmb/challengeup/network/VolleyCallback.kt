package fr.usmb.challengeup.network

/**
 * Interface pour gérer les réponses des requêtes HTTP avec Volley.
 *
 * Cette interface définit deux méthodes de rappel (callback) qui sont appelées
 * lorsque la requête HTTP réussit ou échoue.
 */
interface VolleyCallback {

    /**
     * Appelée lorsque la requête HTTP réussit.
     *
     * @param result Le résultat de la requête HTTP sous forme de chaîne de caractères.
     */
    fun onSuccess(result: String)

    /**
     * Appelée lorsque la requête HTTP échoue.
     */
    fun onError()
}