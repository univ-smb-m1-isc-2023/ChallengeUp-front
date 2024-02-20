package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Connexion"

        val loginButton =findViewById<Button>(R.id.login)

        loginButton.setOnClickListener {
            Snackbar
                .make(loginButton, "Vous avez cliquer sur \"Se connecter\"", Snackbar.LENGTH_SHORT)
                .show()
        }
    }
}