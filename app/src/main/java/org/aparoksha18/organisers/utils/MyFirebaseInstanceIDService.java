package org.aparoksha18.organisers.utils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by akshat on 26/2/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private String refreshedToken;
    @Override
    public void onTokenRefresh() {
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
    }

    public String getRefreshedToken() {
        return refreshedToken;
    }
}