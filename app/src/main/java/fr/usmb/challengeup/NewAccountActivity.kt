package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import fr.usmb.challengeup.utils.UserFeedbackInterface

class NewAccountActivity : AppCompatActivity(), UserFeedbackInterface {
    private lateinit var view: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_account)
        supportActionBar?.setBackgroundDrawable(null)
        title = "Création de compte"
        view = findViewById(R.id.newAccountView)

        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.newAccountStayConnected)
        val createAccountButton = findViewById<Button>(R.id.createAccount)

        stayConnectedSwitch.isChecked = true
        createAccountButton.setOnClickListener { createAccount() }
    }

    private fun createAccount() {
        val email = findViewById<TextInputEditText>(R.id.newAccountEmail).text.toString()
        val username = findViewById<TextInputEditText>(R.id.newAccountUsername).text.toString()
        val password = findViewById<TextInputEditText>(R.id.newAccountPassword).text.toString()
        val confirmPassword = findViewById<TextInputEditText>(R.id.newAccountConfirmPassword).text.toString()

        val isOk : String = if (password.equals(confirmPassword)
                && email.isNotEmpty()
                && username.isNotEmpty()) "oui" else "non"

        showSnackbarMessage(view, isOk)
    }
}