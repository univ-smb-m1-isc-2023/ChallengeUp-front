package fr.usmb.challengeup

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.ConnectionManager
import fr.usmb.challengeup.utils.UserFeedbackInterface

/**
 * Activité de connexion
 */
class MainActivity : AppCompatActivity(), UserFeedbackInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Connexion"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val loginButton = findViewById<Button>(R.id.login)
        val logWithSocialLoginButton = findViewById<Button>(R.id.logWithSocialLogin)
        val joinButton = findViewById<ExtendedFloatingActionButton>(R.id.joinButton)
        val username = findViewById<TextInputEditText>(R.id.usernameValue)
        val password = findViewById<TextInputEditText>(R.id.passwordValue)
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)

        // le bouton "Rester connecté" doit être coché par défaut
        stayConnectedSwitch.isChecked = true
        //stayConnectedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->  }

        loginButton.setOnClickListener {
            val connectionManager = ConnectionManager(
                username.text.toString(),
                password.text.toString(),
                stayConnectedSwitch.isChecked
            )
            val user : User? = connectionManager.doConnection()
            if (user != null) {
                showToastMessage(applicationContext, "Connexion réussie")
                connectionGranted(user)
            } else {
                showSnackbarMessage(loginButton, "Il manque des informations...")
            }
        }

        logWithSocialLoginButton.setOnClickListener {
            connectionGranted(User(0, "Jean-Eudes", "jean-eudes@mail.fr"))
        }

        joinButton.setOnClickListener {
            intent = Intent(applicationContext, NewAccountActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Connexion réussie et passage à l'accueil de l'application
     */
    private fun connectionGranted(user: User?){
        intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
        // une fois connecté, on n'a plus besoin de retourner sur l'activité de connexion donc on la détruit
        finish()
    }
}