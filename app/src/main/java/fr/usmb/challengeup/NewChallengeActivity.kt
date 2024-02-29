package fr.usmb.challengeup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NewChallengeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_challenge)
        supportActionBar?.hide()
    }
}