package fr.usmb.challengeup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.usmb.challengeup.adapter.ChallengeListAdapter
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.entities.Periodicity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SuggestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SuggestionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suggestions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.suggestedChallengeList)
        recyclerView.adapter = ChallengeListAdapter(createTestChallengeList(), true)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SuggestionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SuggestionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    /**
     * Fonction d'essai de la RecyclerView
     */
    private fun createTestChallengeList() : List<Challenge> {
        return listOf(
            Challenge(1,"Courir 7 km","Sport", Periodicity.QUOTIDIEN, "Faire le tour du pâté de maison..."),
            Challenge(2,"Lire 50 pages par jour","Culture", Periodicity.QUOTIDIEN, "Choisissez un livre et lisez 50 pages chaque jour."),
            Challenge(3,"Méditer pendant 15 minutes","Sport", Periodicity.QUOTIDIEN, "Trouvez un endroit calme et méditez pendant 15 minutes."),
            Challenge(4,"Apprendre une nouvelle recette","Cuisine", Periodicity.HEBDOMADAIRE, "Choisissez une recette que vous n'avez jamais essayée et apprenez à la cuisiner."),
            Challenge(5,"Écrire un journal","Culture", Periodicity.QUOTIDIEN, "Prenez le temps de réfléchir à votre journée et écrivez vos pensées dans un journal."),
            Challenge(6,"Faire 10000 pas","Sport", Periodicity.QUOTIDIEN, "Marchez ou courez jusqu'à atteindre 10000 pas."),
            Challenge(7,"Apprendre une nouvelle langue","Culture", Periodicity.MENSUEL, "Choisissez une langue que vous aimeriez apprendre et consacrez du temps chaque jour à son apprentissage.")
        )
    }
}