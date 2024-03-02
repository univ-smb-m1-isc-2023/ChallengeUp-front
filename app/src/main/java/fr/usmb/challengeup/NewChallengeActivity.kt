package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout

class NewChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge)
        supportActionBar?.hide()

        //val tagTextView = findViewById<TextInputLayout>(R.id.newChallengeTag)
        val tags = arrayOf("Sport", "Culture", "Cuisine")
        //(tagTextView.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(tags)
    }
}