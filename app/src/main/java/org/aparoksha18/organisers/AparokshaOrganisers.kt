package org.aparoksha18.organisers

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Created by akshat on 26/2/18.
 */

class AparokshaOrganisers: Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().subscribeToTopic("all")

    }
}