package fr.usmb.challengeup.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import fr.usmb.challengeup.R
import fr.usmb.challengeup.entities.User

class UserPreferencesManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    fun getUserFromSharedPrefs(): User? {
        val json = sharedPreferences.getString(context.getString(R.string.preference_user_key), null)
        val gson = Gson()
        return gson.fromJson(json, User::class.java)
    }

    fun saveUserToSharedPrefs(user: User?) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val json = user?.toJSON()
        editor.putString(context.getString(R.string.preference_user_key), json)
        editor.apply()
    }
}
