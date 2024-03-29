package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONObject

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
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.newAccountStayConnected)

        val isValidUser : Boolean = (
                password.equals(confirmPassword) && password.length > 8
                && email.length > 5 && email.contains("@") && email.contains(".")
                && username.isNotEmpty()
                )

        if (isValidUser)
            createAccountReaquest(User(0, username, email, password), object : VolleyCallback {
                override fun onSuccess(result: String) {
                    showToastMessage(applicationContext, result)
                }

                override fun onError() {
                    showSnackbarMessage(stayConnectedSwitch,
                        "Un utilisateur avec ce nom d'utilisateur ou cet email existe déjà")
                }
            })
        else
            showSnackbarMessage(view, "Il manque des informations ou certaines données sont fausses.")
    }

    fun createAccountReaquest(user: User, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "${getString(R.string.server_domain)}/signup"

        val jsonBody = JSONObject()
        jsonBody.put("username", user.username)
        jsonBody.put("email", user.email)
        jsonBody.put("password", user.password)

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response: JSONObject? -> callback.onSuccess(response.toString()) },
            { callback.onError() }
        )

        queue.add(request)
    }
}