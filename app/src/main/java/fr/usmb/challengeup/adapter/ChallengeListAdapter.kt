package fr.usmb.challengeup.adapter

import android.content.Context
import android.graphics.Color
import fr.usmb.challengeup.utils.DownloadImageTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.R
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface

class ChallengeListAdapter(
    private val context: Context,
    private var dataset: List<Challenge>,
    private val isSuggestions: Boolean // Savoir si c'est l'adapter du Dashboard ou des Suggestions
) : RecyclerView.Adapter<ChallengeListAdapter.ChallengeListViewHolder>(), UserFeedbackInterface {

    private var user: User? = null

    class ChallengeListViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val cardChallenge : MaterialCardView = view.findViewById(R.id.cardChallenge)
        val image : ImageView = view.findViewById(R.id.challengeImage)
        val title : TextView = view.findViewById(R.id.challengeTitle)
        val periodicity : TextView = view.findViewById(R.id.challengePeriodicity)
        val tag : TextView = view.findViewById(R.id.challengeTag)
        val description : TextView = view.findViewById(R.id.challengeDescription)
        val removeButton: Button = view.findViewById(R.id.challengeRemove)
        val accomplishedButton : Button = view.findViewById(R.id.challengeAccomplished)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeListViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.challenge_item, parent, false)
        return ChallengeListViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ChallengeListViewHolder, position: Int) {
        val challenge : Challenge = dataset[position]
        holder.title.text = challenge.title
        holder.periodicity.text = challenge.periodicity.toString()
        holder.tag.text = challenge.tag
        holder.description.text = challenge.description
        //holder.image.setImageResource(R.drawable.ic_sports)
        var url: String
        when (challenge.tag) {
            "Sport" -> url = "https://static.data.gouv.fr/images/2015-01-22/2578753de17a456a85422d14d31ea289/Sport_balles.png"
            "Cuisine" -> url = "https://resize.programme-television.org/original/var/premiere/storage/images/tele-7-jours/news-tv/furieux-contre-un-restaurateur-philippe-etchebest-claque-la-porte-de-cauchemar-en-cuisine-ce-mec-n-en-a-rien-a-secouer-4682958/99713581-1-fre-FR/Furieux-contre-un-restaurateur-Philippe-Etchebest-claque-la-porte-de-Cauchemar-en-cuisine-Ce-mec-n-en-a-rien-a-secouer.png"
            else -> url = "https://absolumentchats.com/wp-content/uploads/2016/12/35927559_web-1-1.jpg"
        }
        DownloadImageTask(holder.image).downloadImage(url)

        if (challenge.reported)
            holder.cardChallenge.setBackgroundColor(Color.RED)

        if (isSuggestions) {
            holder.removeButton.text = holder.view.context.getString(R.string.report)
            holder.accomplishedButton.text = holder.view.context.getString(R.string.subscribe)
        }

        holder.removeButton.setOnClickListener {
            if (isSuggestions)
                report(challenge)
            else showSnackbarMessage(holder.title, holder.removeButton.text.toString(), Snackbar.LENGTH_SHORT)
        }
        
        holder.accomplishedButton.setOnClickListener {
            if (isSuggestions) {
                subscribe(challenge)
            } else showSnackbarMessage(holder.title, holder.accomplishedButton.text.toString(), Snackbar.LENGTH_SHORT)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    private fun subscribe(challenge: Challenge) {
        val sharedPreferencesManager = SharedPreferencesManager(context)
        user = sharedPreferencesManager.getUserFromSharedPrefs()
        subscribeRequest(challenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                TODO("Not yet implemented")
            }
            override fun onError() {
                TODO("Not yet implemented")
            }
        })
    }

    private fun report(challenge: Challenge) {
        val position = dataset.indexOf(challenge)
        val newDataset = ArrayList<Challenge>(dataset)
        /*newDataset.removeAt(position)
        dataset = newDataset.toList()
        notifyItemRemoved(position)*/
        challenge.reported = !challenge.reported
        newDataset[position] = challenge
        dataset = newDataset.toList()
        notifyItemChanged(position)

        /*reportRequest(challenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                showToastMessage(context, "Ce challenge a bien été signalé")
            }
            override fun onError() {
                showToastMessage(context, "Impossible de signaler ce challenge")
            }
        })*/
    }

    private fun subscribeRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val uid = user?.id
        val cid = challenge.id
        val url = "${context.getString(R.string.server_domain)}/user/$uid/subscribe/$cid"
    }

    private fun reportRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val cid = challenge.id
        val url = "${context.getString(R.string.server_domain)}/challenge/report/$cid"
    }
}