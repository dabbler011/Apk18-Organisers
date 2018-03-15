package org.aparoksha18.organisers.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.fragment_new.view.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import kotlinx.android.synthetic.main.fragment_new.*
import org.aparoksha18.organisers.R
import org.aparoksha18.organisers.activities.QRScannerActivity
import org.aparoksha18.organisers.utils.AppDB
import org.json.JSONException
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

        scanQRBtn.setOnClickListener {
            initScanner()
        }

        val appDb = AppDB.getInstance(context)

        val sharedPrefs = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        val eventsList = appDb.getAllEvents()
        eventsList.sortBy { it.timestamp }
        val list = eventsList.map { it.name }

        val defaultEvent = sharedPrefs.getString("eventName",list[0])

        val dataAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, list)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        notification_text.setAdapter(dataAdapter)

        notification_text.setSelection(list.indexOf(defaultEvent))

        notification_text.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sharedPrefs.edit().putString("eventName",notification_text.selectedItem.toString()).commit()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
        view.send_button.setOnClickListener { v ->

            val name = sharedPrefs.getString("eventName",defaultEvent)
            val description = view.description_text.text.toString()
            if( name != "" && description != "") {
                sendNotification(name,description, eventsList.find { it.name.equals(name) }!!.id)
                view.description_text.setText("")
            } else {
                Toast.makeText(activity,"message is empty",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initScanner() {
        val qrScan = IntentIntegrator(activity)
        qrScan.setOrientationLocked(false)
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        qrScan.setPrompt("Scan QR of the user")
        qrScan.captureActivity = QRScannerActivity::class.java
        qrScan.initiateScan()
    }

    fun sendNotification(message: String,description: String,eventID: Long) {
        val dialog = ProgressDialog(activity)
        dialog.setTitle("Sending notification...")
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.show()

        doAsync {

            val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!;

            val client = OkHttpClient()

            val bodyString = "{\"description\":\"" + description +
                    "\",\"senderName\":\"" + FirebaseAuth.getInstance().currentUser!!.email +
                    "\",\"timestamp\":" + System.currentTimeMillis() + ",\"title\":\"" + message +
                    "\",\"verified\":" + false + ",\"eventID\":" + eventID + "}"

            val body = RequestBody.create(JSON, bodyString)
            try {
                val request = Request.Builder()
                        .url("https://aparoksha-18.firebaseio.com/notifications.json")
                        .post(body)
                        .build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    uiThread {
                        dialog.dismiss()
                        toast("Waiting for admins approval...")
                    }
                } else {
                    uiThread {
                        dialog.dismiss()
                        Log.d("akshat",response.message())
                        Toast.makeText(activity, "Unsuccessful ", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                uiThread {
                    dialog.dismiss()
                    Toast.makeText(activity, "Exception ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) toast("Result not found")
            else {
                try {
                    val obj = JSONObject(result.contents)

                    toast(obj.toString())

                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val email = obj.getString("email")

                    toast(id+name+email)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}