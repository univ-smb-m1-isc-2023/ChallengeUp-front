package fr.usmb.challengeup

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * Activité de connexion
 */
class MainActivity : AppCompatActivity(), UserFeedbackInterface {
    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbLoginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // printHashKey()
        title = "Connexion"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val loginButton = findViewById<Button>(R.id.login)
        val logWithSocialLoginButton = findViewById<Button>(R.id.logWithSocialLogin)
        val fbLoginButton = findViewById<LoginButton>(R.id.facebookLogin)
        val joinButton = findViewById<ExtendedFloatingActionButton>(R.id.joinButton)
        val username = findViewById<TextInputEditText>(R.id.usernameValue)
        val password = findViewById<TextInputEditText>(R.id.passwordValue)
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)

        checkIsAlreadyAuthenticated()

        // le bouton "Rester connecté" doit être coché par défaut
        stayConnectedSwitch.isChecked = true
        //stayConnectedSwitch.setOnCheckedChangeListener { buttonView, isChecked ->  }

        loginButton.setOnClickListener {
            val username = username.text.toString()
            val password = password.text.toString()
            // cacher le clavier une fois le bouton appuyé
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(loginButton.windowToken, 0)

            if (username.isNotEmpty() && password.isNotEmpty()) {
                var user = User(0, username, null, password)
                // si on donne un mail plutôt qu'un username
                if (username.contains("@") && username.contains(".")) {
                    user = User(0, null, username, password)
                }
                connectionRequest(user, object : VolleyCallback {
                    override fun onSuccess(result: String) {
                        user = User(result.toLong(), user.username, user.email, user.password)
                        connectionGranted(user)
                    }
                    override fun onError() {
                        showSnackbarMessage(loginButton, "Mauvais nom d'utilisateur ou mot de passe")
                    }
                })
            } else {
                showSnackbarMessage(loginButton, "Il manque des informations...")
            }
        }

        logWithSocialLoginButton.setOnClickListener {
            connectionGranted(User(0, "Jean-Eudes", "jean-eudes@mail.fr", null))
        }

        joinButton.setOnClickListener {
            intent = Intent(applicationContext, NewAccountActivity::class.java)
            startActivity(intent)
        }

        // Gestion du FB Login
        fbLogin()
    }

    private fun connectionRequest(user: User, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "${getString(R.string.server_domain)}/auth/login"

        val jsonBody = JSONObject(user.toJSON())
        jsonBody.remove("id")

        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                callback.onSuccess(response.toString())
            },
            Response.ErrorListener {
                callback.onError()
                // showToastMessage(applicationContext, error.message.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray()
            }

            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        queue.add(request)
    }

    /**
     * Connexion réussie et passage à l'accueil de l'application
     */
    private fun connectionGranted(user: User?){
        val stayConnectedSwitch = findViewById<MaterialSwitch>(R.id.stayConnectedSwitch)

        // Si l'utilsateur à cliquer sur "Rester connécté", on enregistre un booléen dans la mémoire du tél,
        // sinon, on le passe à false
        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        sharedPreferencesManager.saveStayConnectedToSharedPrefs(stayConnectedSwitch.isChecked)
        user?.let { sharedPreferencesManager.saveUserToSharedPrefs(it) }

        intent = Intent(applicationContext, HomeActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
        // une fois connecté, on n'a plus besoin de retourner sur l'activité de connexion donc on la détruit
        finish()
    }

    /**
     * Vérifie si l'utilisateur avait coché "Rester connecté"
     * Auquel cas, on le connecte directement en récupérant les données dans les préférences partagées
     */
    private fun checkIsAlreadyAuthenticated() {
        val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
        if (sharedPreferencesManager.getStayConnectedFromSharedPrefs()) {
            val user: User? = sharedPreferencesManager.getUserFromSharedPrefs()
            intent = Intent(applicationContext, HomeActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Facebook Login
     */
    private fun fbLogin() {
        fbCallbackManager = create()
        fbLoginManager = LoginManager.getInstance()

        fbLoginManager.registerCallback(fbCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                }

                override fun onCancel() {
                    // App code
                    showToastMessage(applicationContext, "Vous avez annulé")
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
    }

    /**
     * Pour paramétrer l'application avec le Facebook Login
     */
    private fun printHashKey() {
        // Ajouter du code pour imprimer la clé de hachage
        try {
            val info = packageManager.getPackageInfo(
                "fr.usmb.challengeup",
                PackageManager.GET_SIGNATURES
            )

            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            // Gérer l'exception
        } catch (e: NoSuchAlgorithmException) {
            // Gérer l'exception
        }
    }
}