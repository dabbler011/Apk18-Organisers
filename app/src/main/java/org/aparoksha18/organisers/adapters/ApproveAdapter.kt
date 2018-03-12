package org.aparoksha18.organisers.adapters

import android.app.ProgressDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.pending_container.view.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.aparoksha18.organisers.R
import org.aparoksha18.organisers.models.Notification
import org.aparoksha18.organisers.utils.AppDB
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by akshat on 27/2/18.
 */

class ApproveAdapter : RecyclerView.Adapter<ApproveAdapter.ViewHolder>() {
    var list: MutableMap<String, Notification> = mutableMapOf()
    var keyList = ArrayList<String>()

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        context = parent!!.context
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.pending_container, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list.getValue(keyList[position]),keyList[position],context,this)
    }

    fun updateData(map: Map<String,Notification>){
        list = map as MutableMap<String, Notification>
        keyList.clear()
        keyList.addAll(list.keys)
        notifyDataSetChanged()
    }

    fun remove(key: String) {
        keyList.remove(key)
        list.remove(key)
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun  bindItem(notification: Notification, key: String,context: Context,adapter: ApproveAdapter) {
            itemView.tv_title.text = notification.title
            itemView.tv_description.text = notification.description
            itemView.sender.text = notification.senderName

            itemView.imageButton2.setOnClickListener {
                adapter.remove(key)
                deleteFromPending(key,context)
            }

            itemView.imageButton.setOnClickListener {
                adapter.remove(key)
                sendNotification(notification.title,notification.description,key,context)
            }
        }

        fun sendNotification(message: String, description: String,key: String,context: Context) {
            val dialog = ProgressDialog(context)
            dialog.setTitle("Sending notification...")
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            dialog.show()
            val appDB = AppDB.getInstance(context)
            val events = appDB.getAllEvents()
            val eventID = events.find { it.name.equals(message) }!!.id

            doAsync {
                try {
                    val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!;
                    val client = OkHttpClient()

                    val bodyString = "{\"description\":\"" + description + "\",\"senderName\":\""+
                            FirebaseAuth.getInstance().currentUser!!.email +"\",\"timestamp\":" +
                            System.currentTimeMillis() + ",\"title\":\"" +message + "\",\"verified\":" +
                            true + ",\"eventID\":" + eventID + "}"

                    val body = RequestBody.create(JSON,bodyString)
                    try {
                        val request = Request.Builder()
                                .url("https://aparoksha-18.firebaseio.com/notifications/"+key+".json")
                                .put(body)
                                .build()
                        val response = client.newCall(request).execute()
                        if(response.isSuccessful) {
                            uiThread {
                                dialog.dismiss()
                            }
                        }

                    } catch (e : Exception) {
                        uiThread {
                            dialog.dismiss()
                        }
                    }
                } catch (e: Exception) {
                    uiThread {
                        dialog.dismiss()
                    }
                }
            }
        }

        fun deleteFromPending(key: String,context: Context) {
            val dialog = ProgressDialog(context)
            dialog.setTitle("Processing...")
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            dialog.show()
            doAsync {
                val client = OkHttpClient()

                try {
                    val request = Request.Builder()
                            .url("https://aparoksha-18.firebaseio.com/notifications/"+key+".json")
                            .delete()
                            .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        uiThread {

                            dialog.dismiss()
                        }
                    }

                } catch (e: Exception) {
                    uiThread {
                        dialog.dismiss()
                    }
                }
            }
        }
    }

}