package org.aparoksha18.organisers

import android.app.Activity
import android.app.Fragment
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Message
import android.os.ParcelFileDescriptor
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_new.view.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import org.json.JSONObject

/**
 * Created by akshat on 4/10/17.
 */

class NewFragment :Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_new,container,false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        view.send_button.setOnClickListener { v ->

            val message = view.notification_text.text.toString()
            val description = view.description_text.text.toString()
            if( message != "" && description != "") {
                sendNotification(message,description)
                view.notification_text.setText("")
                view.description_text.setText("")
            } else {
                Toast.makeText(activity,"Either title or description is empty",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun sendNotification(message: String,description: String) {
        val dialog = ProgressDialog(activity)
        dialog.setTitle("Sending notification...")
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.show()

        doAsync {

            val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!;

            val client = OkHttpClient()

            val bodyString = "{\"description\":\"" + description + "\",\"senderName\":\"" + FirebaseAuth.getInstance().currentUser!!.email + "\",\"timestamp\":" + System.currentTimeMillis() + ",\"title\":\"" + message + "\"}"

            val body = RequestBody.create(JSON, bodyString)
            try {
                val request = Request.Builder()
                        .url("https://aparoksha-18.firebaseio.com/pending.json")
                        .post(body)
                        .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    uiThread {
                        dialog.dismiss()
                        toast("Waiting for admins approval...")
                    }
                }

            } catch (e: Exception) {
                uiThread {
                    dialog.dismiss()
                    Toast.makeText(activity, "Unsuccessful ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*fun sendNotification(message: String, description: String) {
        var act = (activity) as MainActivity
        val dialog = ProgressDialog(activity)
        dialog.setTitle("Sending notification...")
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.show()
        doAsync {
            try {
                val GCM_API_KEY = getString(R.string.GCM_KEY)
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
                                    .url("https://effervescence-17.firebaseio.com/notifications.json")
                                    .post(body)
                                    .build()
                            val response = client.newCall(request).execute()
                            if(response.isSuccessful) {
                                uiThread {
                                    dialog.dismiss()
                                    toast("Notification Sent")
                                }
                            }

                        } catch (e : Exception) {
                            uiThread {
                                dialog.dismiss()
                                Toast.makeText(activity,"Unsuccessful ",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                 else {
                    uiThread {
                        dialog.dismiss()
                        Toast.makeText(activity,"Unsuccessful ",Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (e: Exception) {
                uiThread {
                    dialog.dismiss()
                    toast("Unsuccessful")
                }
            }
        }
    }*/
}