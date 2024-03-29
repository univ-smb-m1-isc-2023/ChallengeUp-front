package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.entities.Periodicity
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONObject

class NewChallengeActivity : AppCompatActivity(), UserFeedbackInterface {

    private lateinit var createChallengeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge)
        supportActionBar?.setBackgroundDrawable(null)
        title = applicationContext.getString(R.string.newChallengeLabel)

        val tags = mapOf(
            "Sport" to R.drawable.ic_sports,
            "Culture" to R.drawable.ic_book,
            "Cuisine" to R.drawable.ic_fastfood
        )
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupTag)
        var tagChosen = ""
        for (tag in tags) {
            val chip = Chip(this)
            chip.text = tag.key
            chip.chipIcon =  ContextCompat.getDrawable(this, tag.value)
            chip.isCheckable = true
            chip.isCheckedIconVisible = true
            chip.isChipIconVisible = true
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chip.isChipIconVisible = false
                    tagChosen = tag.key
                } else chip.isChipIconVisible = true
            }
            chipGroup.addView(chip)
        }
        chipGroup.isSingleSelection = true

        val buttonPeriodicityGroup = findViewById<MaterialButtonToggleGroup>(R.id.segmentedButtonPeriodicity)
        buttonPeriodicityGroup.check(R.id.buttonWeekly) // bouton coché par défaut
        var periodicity = Periodicity.HEBDOMADAIRE
        buttonPeriodicityGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.buttonDaily) periodicity = Periodicity.QUOTIDIEN
                else if (checkedId == R.id.buttonWeekly) periodicity = Periodicity.HEBDOMADAIRE
                else periodicity = Periodicity.MENSUEL
            }
        }

        createChallengeButton = findViewById(R.id.createChallenge)
        createChallengeButton.setOnClickListener {
            if (tagChosen.isNotEmpty())
                createNewChallenge(tagChosen, periodicity)
            else
                showSnackbarMessage(createChallengeButton, "Il manque des informations...")
        }
    }

    /**
     * Lorsqu'on appuie sur la flèche de retour
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /**
     * Instancie le challenge et le post sur le serveur
     */
    private fun createNewChallenge(tag: String, periodicity: Periodicity) {
        val name = findViewById<TextInputEditText>(R.id.newChallengeTitleValue).text.toString()
        val description = findViewById<TextInputEditText>(R.id.newChallengeDescriptionValue).text.toString()
        val newChallenge = Challenge(null, name, tag, periodicity, description)
        createNewChallengeRequest(newChallenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                showSnackbarMessage(createChallengeButton, result)
            }
            override fun onError() {
                showSnackbarMessage(createChallengeButton, "Echec de la création du challenge")
            }
        })
        //finish()
    }

    private fun createNewChallengeRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(applicationContext)
        val url = "${getString(R.string.server_domain)}/challenge/create"
        val jsonChallenge = JSONObject(challenge.toJSON())

        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonChallenge,
            { response: JSONObject? ->
                callback.onSuccess(response.toString())
            },
            {
                callback.onError()
            })

        queue.add(request)
    }
}