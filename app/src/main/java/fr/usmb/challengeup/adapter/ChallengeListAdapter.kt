package fr.usmb.challengeup.adapter

import fr.usmb.challengeup.utils.DownloadImageTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.R
import fr.usmb.challengeup.utils.UserFeedbackInterface

class ChallengeListAdapter(
    //private val context: Context, pas besoin de context ici puisque les cards ne nous emmène pas vers de nouvelles Activities
    private val dataset: List<Challenge>,
    private val isSuggestions: Boolean // Savoir si c'est l'adapter du Dashboard ou des Suggestions
) : RecyclerView.Adapter<ChallengeListAdapter.ChallengeListViewHolder>(), UserFeedbackInterface {

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


        if (isSuggestions) {
            holder.removeButton.text = holder.view.context.getString(R.string.report)
            holder.accomplishedButton.text = holder.view.context.getString(R.string.subscribe)
        }

        holder.removeButton.setOnClickListener {
            showSnackbarMessage(holder.title, holder.removeButton.text.toString(), Snackbar.LENGTH_SHORT)
        }
        
        holder.accomplishedButton.setOnClickListener {
            showSnackbarMessage(holder.title, holder.accomplishedButton.text.toString(), Snackbar.LENGTH_SHORT)
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}