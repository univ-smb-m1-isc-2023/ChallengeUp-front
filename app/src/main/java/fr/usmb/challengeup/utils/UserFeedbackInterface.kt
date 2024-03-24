package fr.usmb.challengeup.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

interface UserFeedbackInterface {
    fun showSnackbarMessage(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT){
        Snackbar.make(view, message, duration).show()
    }

    fun showToastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(context, message, duration).show()
    }
}