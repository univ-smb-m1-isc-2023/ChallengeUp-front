package fr.usmb.challengeup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.usmb.challengeup.adapter.ChallengeListAdapter
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.UserFeedbackInterface

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SuggestionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SuggestionsFragment : Fragment(), UserFeedbackInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var vue: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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

        vue = view
        createChallengeList()

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshSuggestions)
        swipeRefreshLayout.setOnRefreshListener { createChallengeList() }
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

    private fun getSuggestedChallengesRequest(callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val url = "${getString(R.string.server_domain)}/challenge/all"
        // val url = "${getString(R.string.server_domain)}/challenge/highestProgress" // marche très bien mais peu de données encore

        val request = object : StringRequest(
            Method.GET, url,
            { response -> callback.onSuccess(response) },
            { callback.onError() }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                val str = String(response.data, Charsets.UTF_8)
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response))
            }
        }
        queue.add(request)
    }

    /**
     * Fonction d'instanciation de la RecyclerView
     */
    fun createChallengeList() {
        val loading = vue.findViewById<LinearProgressIndicator>(R.id.suggestionsChallengeListLoading)
        loading.show()
        var listChallenge = emptyList<Challenge>()
        val listType = object : TypeToken<List<Challenge>>() {}.type // type de la liste
        val gson = Gson()

        getSuggestedChallengesRequest(object : VolleyCallback {
            override fun onSuccess(result: String) {
                context?.let {
                    val challengesFromServer = gson.fromJson<List<Challenge>>(result, listType)
                    if (challengesFromServer.isNotEmpty()) {
                        listChallenge = challengesFromServer
                        listChallenge = listChallenge.sortedBy { challenge -> challenge.reported }
                    }

                    // Instanciation de la RecyclerView avec les données de la requête
                    val recyclerView = vue.findViewById<RecyclerView>(R.id.suggestedChallengeList)
                    recyclerView.adapter = ChallengeListAdapter(context!!, listChallenge, true)
                    loading.hide()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            override fun onError() {
                context?.let {
                    showToastMessage(it, "Erreur serveur...")

                    // Instanciation de la RecyclerView avec des données bidon si le serveur flanche
                    val recyclerView = vue.findViewById<RecyclerView>(R.id.suggestedChallengeList)
                    recyclerView.adapter = ChallengeListAdapter(context!!, listChallenge, false)
                    loading.hide()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }
}