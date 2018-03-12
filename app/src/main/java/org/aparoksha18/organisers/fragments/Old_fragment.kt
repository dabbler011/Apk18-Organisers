package org.aparoksha18.organisers.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_old.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.aparoksha18.organisers.models.Notification
import org.aparoksha18.organisers.R
import org.aparoksha18.organisers.adapters.UpdatesAdapter
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject

/**
 * Created by akshat on 4/10/17.
 */
class Old_fragment: Fragment() {
    private lateinit var updatesAdapter : UpdatesAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_old, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshAdapter(view)

        refresh.setColorSchemeResources(R.color.colorAccent)

        refresh.setOnRefreshListener {
            refreshAdapter(view)
            refresh.isRefreshing = false
        }

        updatesAdapter = UpdatesAdapter()
        updatesRV.adapter = updatesAdapter
        updatesRV.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)

    }


    private fun refreshAdapter(view: View){
        noNotifsTV.visibility = View.GONE
        fetchLatestData(view)
    }

    private fun fetchLatestData(view: View){
        doAsync {
            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("https://aparoksha-18.firebaseio.com/notifications.json")
                    .build()
            val response = client.newCall(request).execute()
            try {
                if (response.isSuccessful) {
                    val updatesList: ArrayList<Notification> = ArrayList()
                    val body = JSONObject(response.body()?.string())
                    val keys = body.keys()

                    while (keys.hasNext()) {
                        val key = keys.next().toString()
                        val childObj = body.getJSONObject(key)
                        if (childObj != null) {
                            val newNotification = Notification()
                            newNotification.description = childObj.getString("description")
                            newNotification.senderName = childObj.getString("senderName")
                            newNotification.timestamp = childObj.getLong("timestamp")
                            newNotification.title = childObj.getString("title")
                            newNotification.eventID = childObj.getInt("eventID")
                            newNotification.verified = childObj.getBoolean("verified")

                            if (newNotification.verified) updatesList.add(newNotification)
                        }
                    }
                    uiThread {
                        noNotifsTV?.visibility = View.GONE
                        updatesRV?.visibility = View.VISIBLE
                        updatesAdapter.updateData(updatesList)
                    }
                }
            } catch (e: Exception) {
                uiThread {
                    noNotifsTV?.visibility = View.VISIBLE
                    noNotifsTV?.text = "No Notifications !!"
                    updatesRV?.visibility = View.GONE
                }
            }
        }
    }
}