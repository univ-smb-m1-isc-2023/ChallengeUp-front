package fr.usmb.challengeup

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val loginButton = findViewById<Button>(R.id.login)
        val logWithSocialLoginButton = findViewById<Button>(R.id.logWithSocialLogin)

        loginButton.setOnClickListener {
            Snackbar
                .make(loginButton, "Vous avez cliqué sur \"Se connecter\"", Snackbar.LENGTH_SHORT)
                .show()
        }

        logWithSocialLoginButton.setOnClickListener {
            intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            // une fois connecté, on n'a plus besoin de retourner sur l'activité de connexion donc on la détruit
            finish()
        }

        // le bouton "Rester connecté" doit être coché par défaut
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)
        stayConnectedSwitch.isChecked = true
        //stayConnectedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->  }
    }
}