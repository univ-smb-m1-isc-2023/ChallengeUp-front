package fr.usmb.challengeup

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.usmb.challengeup.adapter.ChallengeListAdapter
import fr.usmb.challengeup.entities.Challenge
import fr.usmb.challengeup.entities.Progress
import fr.usmb.challengeup.entities.User
import fr.usmb.challengeup.network.VolleyCallback
import fr.usmb.challengeup.utils.SharedPreferencesManager
import fr.usmb.challengeup.utils.UserFeedbackInterface
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment(), UserFeedbackInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var vue: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var user: User? = null

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
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vue = view

        val sharedPreferencesManager = context?.let { SharedPreferencesManager(it) }
        if (sharedPreferencesManager != null) {
            user = sharedPreferencesManager.getUserFromSharedPrefs()
        }

        updateRegularity(0.0)

        val newChallengeButton = view.findViewById<ExtendedFloatingActionButton>(R.id.newChallengeFAB)
        newChallengeButton.setOnClickListener {
            val intent = Intent(context, NewChallengeActivity::class.java)
            startActivity(intent)
        }

        createChallengeList()

        val nestedScrollView = vue.findViewById<NestedScrollView>(R.id.dashboardNestedScrollView)
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                // Scroll Down
                if (newChallengeButton.isExtended) {
                    newChallengeButton.shrink()
                }
            } else if (scrollY < oldScrollY) {
                // Scroll Up
                if (!newChallengeButton.isExtended) {
                    newChallengeButton.extend()
                }
            }
        })

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshDashboard)
        swipeRefreshLayout.setOnRefreshListener { createChallengeList() }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DashboardFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getMyChallengesRequest(callback: VolleyCallback) {
        val queue = Volley.newRequestQueue(context)
        val url = "${getString(R.string.server_domain)}/progress/user/${user?.id}"

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
        val loading = vue.findViewById<LinearProgressIndicator>(R.id.dashboardChallengeListLoading)
        loading.show()
        var listChallenge = emptyList<Challenge>()
        val listType = object : TypeToken<List<Progress>>() {}.type // type de la liste
        val gson = Gson()

        getMyChallengesRequest(object : VolleyCallback {
            override fun onSuccess(result: String) {
                context?.let {
                    var progressFromServer = gson.fromJson<List<Progress>>(result, listType)
                    if (progressFromServer.isNotEmpty()) {
                        progressFromServer = progressFromServer.sortedBy { progress -> progress.completed }
                        // mise à true de reported pour les challenges complétés (c'est juste de l'affichage)
                        for (progress in progressFromServer)
                            if (progress.completed)
                                progress.challenge.reported= true
                        listChallenge = progressFromServer.map { progress -> progress.challenge }

                        val jsonResult = JSONArray(result)
                        val jsonFirstProgressUser = jsonResult.getJSONObject(0).getJSONObject("user")
                        val regularity = jsonFirstProgressUser.getDouble("regularity")
                        updateRegularity(regularity)
                    }

                    // Instanciation de la RecyclerView avec les données de la requête
                    val recyclerView = vue.findViewById<RecyclerView>(R.id.challengeList)
                    recyclerView.adapter = ChallengeListAdapter(context!!, listChallenge, false)
                    val myChallenges = vue.findViewById<TextView>(R.id.myChallengesTitle)
                    if (listChallenge.isNotEmpty())
                        myChallenges.text = "${listChallenge.size} challenge${if(listChallenge.size > 1) "s" else ""}"
                    else
                        myChallenges.text = "Créez un challenge ou ajoutez-en un depuis les suggestions"
                    loading.hide()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
            override fun onError() {
                context?.let {
                    showToastMessage(it, "Erreur serveur...")

                    // Instanciation de la RecyclerView avec des données bidon si le serveur flanche
                    val recyclerView = vue.findViewById<RecyclerView>(R.id.challengeList)
                    recyclerView.adapter = ChallengeListAdapter(context!!, listChallenge, false)
                    val myChallenges = vue.findViewById<TextView>(R.id.myChallengesTitle)
                    myChallenges.text = "${listChallenge.size} challenge${if(listChallenge.size > 1) "s" else ""}"
                    loading.hide()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun updateRegularity(regularity: Double) {
        val progressIndicator = vue.findViewById<CircularProgressIndicator>(R.id.progressRegularity)
        progressIndicator.trackColor = Color.LTGRAY
        val progessAnimatorValue = regularity.toInt()
        if (progessAnimatorValue < 50) progressIndicator.setIndicatorColor(Color.RED)
        else progressIndicator.setIndicatorColor(Color.GREEN)
        val progressAnimator = ValueAnimator.ofInt(0, progessAnimatorValue).apply {
            duration = 1500 // Durée de l'animation en millisecondes
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Int
                progressIndicator.setProgressCompat(progress, false)
            }
        }
        progressAnimator.start()

        val progressComment = vue.findViewById<TextView>(R.id.progressComment)
        if (regularity < 50) progressComment.text = getString(R.string.encouragement)
        else progressComment.text = getString(R.string.congratulations)
    }
}