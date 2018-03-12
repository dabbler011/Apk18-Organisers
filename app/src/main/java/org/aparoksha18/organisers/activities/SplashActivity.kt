package org.aparoksha18.organisers.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.util.Log
import android.view.animation.AnticipateOvershootInterpolator
import com.esotericsoftware.minlog.Log.debug
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.android.synthetic.main.activity_splash.*
import org.aparoksha18.organisers.R
import org.aparoksha18.organisers.models.Event
import org.aparoksha18.organisers.utils.AppDB
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*

class SplashActivity : AppCompatActivity() {


    private lateinit var sharedPrefs: SharedPreferences
    private val RC_SIGN_IN: Int = 23

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        Handler().postDelayed({
            val constraintSet = ConstraintSet()
            constraintSet.clone(this@SplashActivity, R.layout.secondary_splash)

            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator(1.0f)
            transition.duration = 1200

            TransitionManager.beginDelayedTransition(splash, transition)
            constraintSet.applyTo(splash)

        }, 100)

        Handler().postDelayed({
            when {
                isNetworkConnectionAvailable() -> {
                    fetchLatestData()
                }
                !sharedPrefs.getBoolean("firstrun", true) -> {

                }
                else -> {
                    showAlert()
                }
            }
        },2400)
    }

    private fun fetchLatestData() {
        doAsync {
            val appDB = AppDB.getInstance(this@SplashActivity)
            val client = OkHttpClient()


            try {
                val request = Request.Builder()
                        .url("https://aparoksha18.github.io/Aparoksha-Data/events.json")
                        .build()


                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("akshat",response.body()?.string())
                    val list = Moshi.Builder()
                            .build()
                            .adapter<Array<Event>>(Array<Event>::class.java)
                            .fromJson(response.body()?.string())

                    sharedPrefs.edit().putBoolean("firstrun", false).commit()
                    val eventsList: MutableList<Event> = list.toMutableList()
                    eventsList.add(Event(name = "Announcement"))
                    appDB.storeEvents(events = eventsList)
                }

                uiThread {
                    if (!response.isSuccessful) {
                        if (sharedPrefs.getBoolean("firstrun", true)) {
                            showAlert()
                        } else {
                            signInHandler()
                        }
                    } else {

                        signInHandler()
                    }
                }
            } catch (exception: Exception) {
                uiThread {
                    if (sharedPrefs.getBoolean("firstrun", true)) {
                        showAlert()
                    } else {
                        signInHandler()
                    }
                }
            }
        }
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("No internet Connection")
        builder.setMessage("Please turn on internet connection to continue. Internet is needed at least once before running the app.")
        builder.setNegativeButton("close") { _, _ -> finish() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun isNetworkConnectionAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        return if (isConnected) {
            debug("Network Connected")
            true
        } else {
            debug("Network not Connected")
            false
        }
    }

    fun signInHandler() {

        val mFirebaseAuth = FirebaseAuth.getInstance()
        val user = mFirebaseAuth.currentUser

        if (user != null) {

            val db = FirebaseFirestore.getInstance()
            if(user.email.toString().endsWith("@iiita.ac.in",false) || user.email.toString().endsWith("@iiitl.ac.in",false)) {
                db.collection("admins")
                        .document(user.email.toString())
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    startActivity<MainActivity>("admin" to true)
                                    finish()
                                } else {
                                    startActivity<MainActivity>("admin" to false)
                                    finish()
                                }
                            } else {
                                toast("Connectivity Problem")
                                finish()
                            }
                        }
            } else {
                toast("Sign In with college email id")
                AuthUI.getInstance().signOut(this)
                finish()
            }
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(
                                    Arrays.asList<AuthUI.IdpConfig>(
                                            AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) signInHandler() else finish()
        } else {
            finish()
        }
    }
}
