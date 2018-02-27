package org.aparoksha18.organisers

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintSet
import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.view.animation.AnticipateOvershootInterpolator
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 23

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

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
            signInHandler()
        },2400)
    }

    fun signInHandler() {

        val mFirebaseAuth = FirebaseAuth.getInstance()
        val user = mFirebaseAuth.currentUser

        if (user != null) {

            val db = FirebaseFirestore.getInstance()
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
