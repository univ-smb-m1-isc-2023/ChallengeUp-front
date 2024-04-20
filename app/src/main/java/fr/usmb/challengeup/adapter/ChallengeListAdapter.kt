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
import com.android.volley.Request.Method
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
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
        // Réinitialisation du bouton et du fond pour éviter le recyclage de vues signalées pour l'affichage de nouvelles vues "saines"
        holder.cardChallenge.setCardBackgroundColor(Color.WHITE)
        holder.accomplishedButton.visibility = View.VISIBLE
        //holder.image.setImageResource(R.drawable.ic_sports)
        var url: String
        when (challenge.tag) {
            "Sport" -> url = "https://static.data.gouv.fr/images/2015-01-22/2578753de17a456a85422d14d31ea289/Sport_balles.png"
            "Cuisine" -> url = "https://resize.programme-television.org/original/var/premiere/storage/images/tele-7-jours/news-tv/furieux-contre-un-restaurateur-philippe-etchebest-claque-la-porte-de-cauchemar-en-cuisine-ce-mec-n-en-a-rien-a-secouer-4682958/99713581-1-fre-FR/Furieux-contre-un-restaurateur-Philippe-Etchebest-claque-la-porte-de-Cauchemar-en-cuisine-Ce-mec-n-en-a-rien-a-secouer.png"
            else -> url = "https://absolumentchats.com/wp-content/uploads/2016/12/35927559_web-1-1.jpg"
        }
        DownloadImageTask(holder.image).downloadImage(url)

        if (isSuggestions) {
            holder.removeButton.text = holder.view.context.getString(R.string.report)
            holder.accomplishedButton.text = holder.view.context.getString(R.string.subscribe)
        }

        holder.removeButton.setOnClickListener {
            if (isSuggestions) report(challenge)
            else unsubscribe(challenge)
        }
        
        holder.accomplishedButton.setOnClickListener {
            if (isSuggestions) {
                subscribe(challenge)
            } else achievement(challenge)
        }

        if (challenge.reported && isSuggestions) {
            holder.cardChallenge.setCardBackgroundColor(Color.parseColor("#50FF0000"))
            holder.accomplishedButton.visibility = View.GONE
            holder.title.text = "[SIGNALE] ${holder.title.text}"
        }

        if (challenge.completed && !isSuggestions) {
            holder.cardChallenge.setCardBackgroundColor(Color.parseColor("#5000FF00"))
            holder.accomplishedButton.visibility = View.GONE
            holder.title.text = "[ACCOMPLI] ${holder.title.text}"
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    /**
     * Déclarer un challenge accompli
     */
    private fun achievement(challenge: Challenge) {
        val sharedPreferencesManager = SharedPreferencesManager(context)
        user = sharedPreferencesManager.getUserFromSharedPrefs()
        achievementRequest(challenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                showToastMessage(context, "Challenge complété pour la peridode indiquée.")
                challenge.completed = true
                updateFromRecyclerView(challenge)
            }
            override fun onError() {}
        })
    }

    /**
     * S'abonner à un challenge suggéré
     */
    private fun subscribe(challenge: Challenge) {
        val sharedPreferencesManager = SharedPreferencesManager(context)
        user = sharedPreferencesManager.getUserFromSharedPrefs()
        subscribeRequest(challenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                showToastMessage(context, "\"${challenge.title}\" a été ajouté à vos challenges.")
                removeFromRecyclerView(challenge)
            }
            override fun onError() {
                showToastMessage(context, "Une erreur s'est produite...")
            }
        })
    }

    /**
     * Retirer un challenge de sa liste de challenges
     */
    private fun unsubscribe(challenge: Challenge) {
        val sharedPreferencesManager = SharedPreferencesManager(context)
        user = sharedPreferencesManager.getUserFromSharedPrefs()
        unsubscribeRequest(challenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                showToastMessage(context, "\"${challenge.title}\" a été retiré de vos challenges.")
                removeFromRecyclerView(challenge)
            }
            override fun onError() {
                showToastMessage(context, "Une erreur s'est produite...")
            }
        })
    }

    /**
     * Signaler un challenge
     */
    private fun report(challenge: Challenge) {
        reportRequest(challenge, object : VolleyCallback {
            override fun onSuccess(result: String) {
                showToastMessage(context, "Ce challenge a bien été signalé")
                challenge.reported = true
                updateFromRecyclerView(challenge)
            }
            override fun onError() {
                showToastMessage(context, "Impossible de signaler ce challenge")
            }
        })
    }

    private fun achievementRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val uid = user?.id
        val cid = challenge.id
        val url = "${context.getString(R.string.server_domain)}/progress/complete/$uid/$cid/true"

        val request = StringRequest(
            Method.PUT, url,
            { jsonProgress -> callback.onSuccess(jsonProgress.toString()) },
            { error ->
                showToastMessage(context, error.message.toString())
                callback.onError()
            }
        )
        queue.add(request)
    }

    private fun subscribeRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val uid = user?.id
        val cid = challenge.id
        val url = "${context.getString(R.string.server_domain)}/user/$uid/subscribe/$cid"

        val request = StringRequest(
            Method.PUT, url,
            { jsonUser -> callback.onSuccess(jsonUser.toString()) },
            { error ->
                showToastMessage(context, error.message.toString())
                callback.onError()
            }
        )
        queue.add(request)
    }

    private fun unsubscribeRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val uid = user?.id
        val cid = challenge.id
        val url = "${context.getString(R.string.server_domain)}/user/$uid/unsubscribe/$cid"

        val request = StringRequest(
            Method.PUT, url,
            { jsonUser -> callback.onSuccess(jsonUser.toString()) },
            { error ->
                showToastMessage(context, error.message.toString())
                callback.onError()
            }
        )
        queue.add(request)
    }

    private fun reportRequest(challenge: Challenge, callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val cid = challenge.id
        val url = "${context.getString(R.string.server_domain)}/challenge/report/$cid"

        val request = StringRequest(
            Method.PUT, url,
            { jsonChallenge -> callback.onSuccess(jsonChallenge.toString()) },
            { callback.onError() }
        )
        queue.add(request)
    }

    private fun removeFromRecyclerView(challenge: Challenge) {
        val position = dataset.indexOf(challenge)
        val newDataset = dataset.toMutableList()
        newDataset.removeAt(position)
        dataset = newDataset.toList()
        notifyItemRemoved(position)
    }

    private fun updateFromRecyclerView(challenge: Challenge) {
        val position = dataset.indexOf(challenge)
        val newDataset = dataset.toMutableList()
        newDataset[position] = challenge
        dataset = newDataset.toList()
        notifyItemChanged(position)
    }
}