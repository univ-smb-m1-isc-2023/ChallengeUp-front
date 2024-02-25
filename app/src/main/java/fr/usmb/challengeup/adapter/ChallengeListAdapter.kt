package fr.usmb.challengeup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.R
import fr.usmb.challengeup.entities.Periodicity

class ChallengeListAdapter(
    //private val context: Context, pas besoin de context ici puisque les cards ne nous emmène pas vers de nouvelles Activities
    private val dataset: List<Challenge>
) : RecyclerView.Adapter<ChallengeListAdapter.ChallengeListViewHolder>() {

    class ChallengeListViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
        val cardChallenge : MaterialCardView = view.findViewById(R.id.cardChallenge)
        val image : ImageView = view.findViewById(R.id.challengeImage)
        val title : TextView = view.findViewById(R.id.challengeTitle)
        val periodicity : TextView = view.findViewById(R.id.challengePeriodicity)
        val tag : TextView = view.findViewById(R.id.challengeTag)
        val description : TextView = view.findViewById(R.id.challengeDescription)
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
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}