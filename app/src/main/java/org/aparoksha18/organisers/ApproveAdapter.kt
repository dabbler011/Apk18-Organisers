package org.aparoksha18.organisers

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
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by akshat on 27/2/18.
 */

class ApproveAdapter : RecyclerView.Adapter<ApproveAdapter.ViewHolder>() {
    var list: Map<String,Notification> = mapOf()
    var keyList = ArrayList<String>()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        context = parent!!.context
        return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.pending_container, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list.getValue(keyList[position]),keyList[position],context)
    }

    fun updateData(map: Map<String,Notification>){
        list = map
        keyList.clear()
        keyList.addAll(list.keys)
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun  bindItem(notification: Notification, key: String,context: Context) {
            itemView.tv_title.text = notification.title
            itemView.tv_description.text = notification.description

            val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/India"))
            calendar.timeInMillis = notification.timestamp.times(1000L)

            val sdf = SimpleDateFormat("hh:mm a")
            //sdf.timeZone = TimeZone.getTimeZone("Asia/India")

            val time = sdf.format(calendar.time)

            sdf.applyPattern("MMM d")
            itemView.tv_timestamp.text = notification.timestamp.toString()
            itemView.tv_timestamp.text = "$time ${sdf.format(calendar.time)}"

            itemView.imageButton2.setOnClickListener {
                deleteFromPending(key,context)
            }

            itemView.imageButton.setOnClickListener {
                sendNotification(notification.title,notification.description,key,context)
            }
        }

        fun sendNotification(message: String, description: String,key: String,context: Context) {
            val dialog = ProgressDialog(context)
            dialog.setTitle("Sending notification...")
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            dialog.show()
            doAsync {
                try {
                    val GCM_API_KEY = context.getString(R.string.GCM_KEY)
                    val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!

                    val client = OkHttpClient()

                    val bodyString = "{\n" +
                            "  \"to\": \"/topics/all\",\n" +
                            "   \"priority\": \"high\",\n" +
                            "  \"notification\": {\n" +
                            "    \"title\": \"" + message + "\","  +
                            "    \"body\": \"" + description + "\"," +
                            "  \"sound\": \"default\"," +
                            "   }\n" +
                            "}"

                    val body = RequestBody.create(JSON,bodyString)

                    val request = Request.Builder()
                            .url("https://fcm.googleapis.com/fcm/send")
                            .header("Content-Type","application/json")
                            .header("Authorization", "key=" + GCM_API_KEY)
                            .post(body)
                            .build()
                    val response = client.newCall(request).execute()
                    if(response.isSuccessful){
                        val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!;

                        val client = OkHttpClient()

                        val bodyString = "{\"description\":\"" + description + "\",\"senderName\":\""+ FirebaseAuth.getInstance().currentUser!!.email +"\",\"timestamp\":" + System.currentTimeMillis() + ",\"title\":\"" +message + "\"}"

                        val body = RequestBody.create(JSON,bodyString)
                        try {
                            val request = Request.Builder()
                                    .url("https://aparoksha-18.firebaseio.com/notifications.json")
                                    .post(body)
                                    .build()
                            val response = client.newCall(request).execute()
                            if(response.isSuccessful) {
                                uiThread {
                                    dialog.dismiss()
                                    deleteFromPending(key,context)
                                }
                            }

                        } catch (e : Exception) {
                            uiThread {
                                dialog.dismiss()
                            }
                        }
                    }
                    else {
                        uiThread {
                            dialog.dismiss()
                        }
                    }
                }catch (e: Exception) {
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
                            .url("https://aparoksha-18.firebaseio.com/pending/"+key+".json")
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