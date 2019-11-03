package com.trinhbk.lecturelivestream.youtube;

import android.accounts.Account;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import java.util.Collections;

import static com.trinhbk.lecturelivestream.ui.signin.LoginActivity.SCOPE_YOUTUBE;

public class YouTubeNewSingleton {

    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    // Global instance of the JSON factory
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private YouTube youtube;
    public static YouTubeNewSingleton newInstance(String mAccount, Context context) {
        return new YouTubeNewSingleton(mAccount,context);
    }

    private YouTubeNewSingleton(String mAccount, Context context) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context,
                Collections.singleton(SCOPE_YOUTUBE));
        credential.setSelectedAccount(new Account(mAccount,"com.google"));
         youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                credential).setApplicationName("LectureLiveStream")
                .build();
    }

    public YouTube getYoutube() {
        return youtube;
    }
}