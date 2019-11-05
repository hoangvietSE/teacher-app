package com.trinhbk.lecturelivestream.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.home.HomeActivity;
import com.trinhbk.lecturelivestream.utils.NetworkUtil;

import java.lang.ref.WeakReference;

public class SplashActivity extends BaseActivity {
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetworkUtil.isConnectedToNetwork(this)){
            showCautionDialog(getResources().getString(R.string.splash_no_internet_connection_warning),"", liveDialog ->{
                liveDialog.dismiss();
                startActivity(new Intent(this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            });
        }else{
            handler = new Handler(getMainLooper());
            handler.postDelayed(new SplashRunnable(this), 1000);
        }
    }

    private static class SplashRunnable implements Runnable {
        private final WeakReference<Activity> activityWeakReference;

        SplashRunnable(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            Activity activity = activityWeakReference.get();
            activity.startActivity(new Intent(activity, HomeActivity.class));
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
        }
    }
}
