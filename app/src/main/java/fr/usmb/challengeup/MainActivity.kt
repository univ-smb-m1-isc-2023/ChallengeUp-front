package fr.usmb.challengeup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Connexion"

        val loginButton = findViewById<Button>(R.id.login)
        val logWithGoogleButton = findViewById<Button>(R.id.logWithGoogle)

        loginButton.setOnClickListener {
            Snackbar
                .make(loginButton, "Vous avez cliqué sur \"Se connecter\"", Snackbar.LENGTH_SHORT)
                .show()
        }

        logWithGoogleButton.setOnClickListener {
            intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
        }

        // le bouton "Rester connecté" doit être coché par défaut
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)
        stayConnectedSwitch.isChecked = true
        //stayConnectedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->  }
    }
}