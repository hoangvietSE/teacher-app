package com.trinhbk.lecturelivestream.application;

import android.app.Application;
import android.content.Context;

import com.deploygate.sdk.DeployGate;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.pedro.rtsp.utils.TLSSocketFactory;
import com.trinhbk.lecturelivestream.network.LiveSiteService;
import com.trinhbk.lecturelivestream.utils.AppPreferences;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainApplication extends Application {

    public static Context instance;

    private static LiveSiteService liveSiteService;

    private OkHttpClient httpClient;

    public static LiveSiteService getLiveSiteService() {
        return liveSiteService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppPreferences.init(this);
        instance = this;
        try {
            initOkHttpClient();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        initLiveSite();
        initDeploydate();
    }

    private void initDeploydate() {
        DeployGate.install(this);
    }

    private void initOkHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        SSLSocketFactory sslSocketFactory = new TLSSocketFactory();


        httpClient = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, trustManager)
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }


    private void initLiveSite() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v2.convertapi.com/convert/")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        liveSiteService = retrofit.create(LiveSiteService.class);
    }
}
