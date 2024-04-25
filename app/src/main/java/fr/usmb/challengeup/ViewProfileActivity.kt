package fr.usmb.challengeup

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.entities.Progress
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.DownloadImageTask
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONArray
import org.json.JSONObject

class ViewProfileActivity : AppCompatActivity(), UserFeedbackInterface {
    private lateinit var user: User
    private var regularity: Double = 0.0
    private var email: String = ""
    private var usernameToVisit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)
        supportActionBar?.setBackgroundDrawable(null)
        usernameToVisit = intent.getStringExtra("viewUser")
        if (usernameToVisit == null) {
            val sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            user = sharedPreferencesManager.getUserFromSharedPrefs()!!
        } else {
            user = User(-1, usernameToVisit, null, null)
        }

        val profileUsername = findViewById<TextView>(R.id.profileUsername)
        user.username?.let {
            watchProfileRequest(it, object : VolleyCallback {
                override fun onSuccess(result: String) {
                    val jsonUser = JSONObject(result)
                    initActivity(jsonUser)
                    displayOtherInfos(jsonUser.getLong("id"))
                }
                override fun onError() {
                    Snackbar.make(profileUsername, "Vous n'avez rien à faire ici", Snackbar.LENGTH_INDEFINITE)
                        .setBackgroundTint(Color.RED)
                        .setActionTextColor(Color.WHITE)
                        .setAction("RETOUR") { finish() }
                        .show()
                }
            })
        }
    }

    /**
     * Lorsqu'on appuie sur la flèche de retour
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun watchProfileRequest(username: String, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        var url = "${getString(R.string.server_domain)}/user/profile/$username"
        if (usernameToVisit == null)
            url = "${getString(R.string.server_domain)}/user/${user.id}"

        val request = JsonObjectRequest(url,
            { response: JSONObject -> callback.onSuccess(response.toString()) },
            { error ->
                val networkResponse = error.networkResponse
                if (networkResponse?.data != null) {
                    val code = networkResponse.statusCode
                    val body = String(networkResponse.data)
                    showToastMessage(applicationContext, "Erreur $code : $body")
                } else showToastMessage(applicationContext, error.message.toString())
                callback.onError()
            }
        )
        queue.add(request)
    }

    private fun getUserProgressRequest(uid: Long, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "${getString(R.string.server_domain)}/progress/user/$uid"

        val request = StringRequest(
            Method.GET, url,
            { jsonProgress -> callback.onSuccess(jsonProgress.toString()) },
            { callback.onError() }
        )
        queue.add(request)
    }

    private fun initActivity(jsonUser: JSONObject) {
        regularity = jsonUser.getDouble("regularity")
        email = jsonUser.getString("email")
        title = "Profil de ${user.username}"

        val profileUsername = findViewById<TextView>(R.id.profileUsername)
        profileUsername.text = user.username

        val profileEmail = findViewById<TextView>(R.id.profileEmail)
        profileEmail.text = email
        val profileRegularity = findViewById<TextView>(R.id.profileRegularityValue)
        profileRegularity.text = "${getString(R.string.regularity)} : ${regularity.toInt()} %"

        val progressIndicator = findViewById<CircularProgressIndicator>(R.id.profileProgressRegularity)
        progressIndicator.trackColor = Color.LTGRAY
        val progessAnimatorValue = regularity.toInt()

        if (progessAnimatorValue < 50) progressIndicator.setIndicatorColor(Color.RED)
        else progressIndicator.setIndicatorColor(Color.GREEN)

        val progressAnimator = ValueAnimator.ofInt(0, progessAnimatorValue).apply {
            duration = 1500 // Durée de l'animation en millisecondes
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressIndicator.setProgressCompat(progress, false)
            }
        }
        progressAnimator.start()
    }

    private fun displayOtherInfos(uid: Long) {
        val otherInfosLayout = findViewById<LinearLayout>(R.id.profileOtherInfosLayout)
        otherInfosLayout.visibility = View.VISIBLE
        val popularCategory = findViewById<TextView>(R.id.profilePopularCategory)
        val numberOfChallenges = findViewById<TextView>(R.id.profileNumberOfChallenges)

        getUserProgressRequest(uid, object : VolleyCallback {
            override fun onSuccess(result: String) {
                val jsonProgress = JSONArray(result)
                val progressList = getListProgressFromJSON(jsonProgress)
                val mostPopularCategory : String? = progressList
                    .groupingBy { progress -> progress.challenge.tag }
                    .eachCount()
                    .maxByOrNull { map -> map.value }
                    ?.key
                if (mostPopularCategory != null)
                    popularCategory.text = "Catégorie favorite : $mostPopularCategory"
                else
                    popularCategory.visibility = View.GONE
                displayCategoryPicture(mostPopularCategory)
                if (usernameToVisit != null) {
                    numberOfChallenges.visibility = View.VISIBLE
                    numberOfChallenges.text = "${user.username} a souscrit à ${progressList.size} " +
                            "challenge${if (progressList.size > 1) "s" else ""}"
                }
            }
            override fun onError() {
                showSnackbarMessage(otherInfosLayout, "Aucune information supplémentaire à afficher")
                otherInfosLayout.visibility = View.GONE
            }
        })
    }

    private fun getListProgressFromJSON(jsonProgress: JSONArray): List<Progress> {
        val progressList = mutableListOf<Progress>()
        for (i in 0 until jsonProgress.length()) {
            val jsonObj = jsonProgress.getJSONObject(i)
            val gson = Gson()
            val progress = Progress(
                jsonObj.getLong("id"),
                jsonObj.getString("date"),
                gson.fromJson(jsonObj.getJSONObject("challenge").toString(), Challenge::class.java),
                gson.fromJson(jsonObj.getJSONObject("user").toString(), User::class.java),
                jsonObj.getBoolean("completed")
            )
            progressList.add(progress)
        }
        return progressList.toList()
    }

    private fun displayCategoryPicture(category: String?) {
        val popularCategoryPic = findViewById<ImageView>(R.id.profilePicturePopularCategory)
        if (category.isNullOrBlank()) {
            popularCategoryPic.visibility = View.GONE
            return
        }
        val url: String
        when (category) {
            "Sport" -> url = "https://static.data.gouv.fr/images/2015-01-22/2578753de17a456a85422d14d31ea289/Sport_balles.png"
            "Cuisine" -> url = "https://resize.programme-television.org/original/var/premiere/storage/images/tele-7-jours/news-tv/furieux-contre-un-restaurateur-philippe-etchebest-claque-la-porte-de-cauchemar-en-cuisine-ce-mec-n-en-a-rien-a-secouer-4682958/99713581-1-fre-FR/Furieux-contre-un-restaurateur-Philippe-Etchebest-claque-la-porte-de-Cauchemar-en-cuisine-Ce-mec-n-en-a-rien-a-secouer.png"
            else -> url = "https://absolumentchats.com/wp-content/uploads/2016/12/35927559_web-1-1.jpg"
        }
        DownloadImageTask(popularCategoryPic).downloadImage(url)

        // image arrondie
        val radius = 60f
        val shapeAppearanceModel = ShapeAppearanceModel()
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        val shapeDrawable = ShapeDrawable(OvalShape())
        shapeDrawable.paint.color = Color.WHITE
        val backgroundDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
            fillColor = ColorStateList.valueOf(Color.WHITE)
        }
        popularCategoryPic.background = backgroundDrawable
        popularCategoryPic.clipToOutline = true
    }
}