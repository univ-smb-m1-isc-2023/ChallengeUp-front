package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.entities.Periodicity
import fr.usmb.challengeup.utils.UserFeedbackInterface

class NewChallengeActivity : AppCompatActivity(), UserFeedbackInterface {

    private lateinit var createChallengeButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge)
        //supportActionBar?.hide()
        supportActionBar?.setBackgroundDrawable(null)
        title = applicationContext.getString(R.string.newChallengeLabel)

        //val tagTextView = findViewById<TextInputLayout>(R.id.newChallengeTag)
        val tags = arrayOf("Sport", "Culture", "Cuisine")
        //(tagTextView.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(tags)

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
           createNewChallenge(tags, periodicity)
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
    private fun createNewChallenge(tags: Array<String>, periodicity: Periodicity) {
        val name = findViewById<TextInputEditText>(R.id.newChallengeTitleValue).text.toString()
        val description = findViewById<TextInputEditText>(R.id.newChallengeDescriptionValue).text.toString()
        val newChallenge = Challenge(-1, name, tags[0], periodicity, description)
        showSnackbarMessage(createChallengeButton, newChallenge.toJSON(), Snackbar.LENGTH_SHORT)
    }
}