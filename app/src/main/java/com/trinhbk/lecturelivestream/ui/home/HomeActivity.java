package com.trinhbk.lecturelivestream.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.YouTube;
import com.squareup.picasso.Picasso;
import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.clip.ListVideoActivity;
import com.trinhbk.lecturelivestream.ui.signin.LoginActivity;
import com.trinhbk.lecturelivestream.ui.teacher.TeacherActivity;
import com.trinhbk.lecturelivestream.utils.AppPreferences;
import com.trinhbk.lecturelivestream.utils.Constants;
import com.trinhbk.lecturelivestream.utils.PermissionUtil;
import com.trinhbk.lecturelivestream.youtube.EventData;
import com.trinhbk.lecturelivestream.youtube.YouTubeApi;
import com.trinhbk.lecturelivestream.youtube.YouTubeNewSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.trinhbk.lecturelivestream.utils.Constants.IntentKey.ACCOUNT;

/**
 * Created by TrinhBK on 8/29/2018.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();
    public static final int REQUEST_CODE_PERMISSION = 9001;

    private LinearLayout llProfile;
    private LinearLayout llLiveStream;
    private LinearLayout llCreateVideo;
    private TextView tvSignIn;
    private TextView tvName;
    private ImageView ivAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initEvent();
        requestPermission();
    }


    private void initView() {
        llLiveStream = findViewById(R.id.llLiveStream);
        llCreateVideo = findViewById(R.id.llCreateVideo);
        llProfile = findViewById(R.id.llHomeProfile);
        tvSignIn = findViewById(R.id.tvHomeSignIn);
        tvName = findViewById(R.id.tvHomeName);
        ivAvatar = findViewById(R.id.ivHomeAvatar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideLoading();
    }

    private void initEvent() {
        changeUI(AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LOGINED));
        llLiveStream.setOnClickListener(this);
        llCreateVideo.setOnClickListener(this);
        llProfile.setOnClickListener(this);
    }

    private void requestPermission() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO};
        if (!PermissionUtil.getInstance().hasPermission(permissions)) {
            PermissionUtil.getInstance().reuqestPermission(this, permissions, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults != null && grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        PermissionUtil.getInstance().goToSettingPermission(this);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llLiveStream:
                if (!AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LOGINED)) {
                    showCautionDialog(getString(R.string.dialog_syntax_login), "", liveDialog -> {
                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivityForResult(intent, Constants.RequestCode.REQUEST_LOGIN);
                    });
                } else {
                    if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.LOGIN_FROM_GOOGLE)) {
                        CreateLiveEventTask createLiveEventTask = new CreateLiveEventTask();
                        createLiveEventTask.execute(AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.ACCOUNT_NAME));
                    } else {
                        if (!AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.USER_ID).isEmpty()) {
                            showLoading();
                            new GraphRequest(
                                    AccessToken.getCurrentAccessToken(),
                                    "/" + AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.USER_ID) + "/live_videos",
                                    null,
                                    HttpMethod.POST,
                                    responseStream -> {
                                        hideLoading();
                                        Log.d(TAG, "onCompleted: " + responseStream);
                                        try {
                                            JSONObject dataStream = responseStream.getJSONObject();
                                            if (dataStream.has("stream_url")) {
                                                AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_FACEBOOK, dataStream.getString("stream_url"));
                                                Intent intent = new Intent(HomeActivity.this, TeacherActivity.class);
                                                startActivity(intent);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                            ).executeAsync();
                        } else {
                            getUserProfile();
                        }
                    }
                }
                break;
            case R.id.llCreateVideo:
                Intent intentList = new Intent(HomeActivity.this, ListVideoActivity.class);
                startActivity(intentList);
                break;
            case R.id.llHomeProfile:
                if (!AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LOGINED)) {
                    Intent intentLogin = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivityForResult(intentLogin, Constants.RequestCode.REQUEST_LOGIN);
                } else {
                    showConfirmDialog(getString(R.string.dialog_confirm_logout), "", liveDialog -> {
                        if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.LOGIN_FROM_FACEBOOK)) {
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                            if (isLoggedIn) {
                                AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.LOGIN_FROM_FACEBOOK, false);
                                AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_FACEBOOK, "");
                                AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.USER_ID, "");
                                LoginManager.getInstance().logOut();
                            }
                        } else {
                            AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.LOGIN_FROM_GOOGLE, false);
                            AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.ACCOUNT_NAME, "");
                            AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_GOOGLE, "");
                        }
                        AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.IS_LOGINED, false);
                        liveDialog.dismiss();
                        changeUI(false);
                    });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.RequestCode.REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                changeUI(AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LOGINED));
            }
        } else if (requestCode == Constants.RequestCode.REQUEST_RECOVERY_ACCOUNT) {
//            changeUI(AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.IS_LOGINED));
        }
    }

    private void changeUI(boolean isLogin) {
        if (isLogin) {
            tvSignIn.setVisibility(View.GONE);
            tvName.setVisibility(View.VISIBLE);
            ivAvatar.setVisibility(View.VISIBLE);
            getUserProfile();
        } else {
            tvSignIn.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.GONE);
            ivAvatar.setVisibility(View.GONE);
        }
    }

    private void getUserProfile() {
        if (AppPreferences.INSTANCE.getKeyBoolean(Constants.KeyPreference.LOGIN_FROM_FACEBOOK)) {
            Bundle params = new Bundle();
            params.putString("fields", "id,name,cover,picture.type(large)");
            showLoading();
            new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                    response -> {
                        hideLoading();
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    String profilePicUrl = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    Picasso.get().load(profilePicUrl).into(ivAvatar);
                                }
                                if (data.has("name")) {
                                    tvName.setText(data.getString("name"));
                                }
                                if (data.has("id")) {
                                    AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.USER_ID, data.getString("id"));
                                    new GraphRequest(
                                            AccessToken.getCurrentAccessToken(),
                                            "/" + data.getString("id") + "/live_videos",
                                            null,
                                            HttpMethod.POST,
                                            responseStream -> {
                                                Log.d(TAG, "onCompleted: " + responseStream);
                                                try {
                                                    JSONObject dataStream = responseStream.getJSONObject();
                                                    if (dataStream.has("stream_url")) {
                                                        AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_FACEBOOK, dataStream.getString("stream_url"));
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                    ).executeAsync();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).executeAsync();

        } else {
            if (!AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.ACCOUNT_NAME).isEmpty()) {
                tvName.setText(AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.ACCOUNT_NAME));
            }
        }
    }

    private class CreateLiveEventTask extends AsyncTask<String, Void, EventData> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected EventData doInBackground(String... accounts) {
            try {
                YouTube youTube = YouTubeNewSingleton.newInstance(accounts[0], HomeActivity.this).getYoutube();
                String date = new Date().toString();
                YouTubeApi.createLiveEvent(youTube, "Event - " + date,
                        "A live streaming event - " + date);
                return YouTubeApi.getLiveCurrentEvent(youTube);

            } catch (UserRecoverableAuthIOException userRecoverableException) {
                Log.w(TAG, "getSubscription:recoverable exception", userRecoverableException);
                startActivityForResult(userRecoverableException.getIntent(), Constants.RequestCode.REQUEST_RECOVERY_ACCOUNT);
            } catch (IOException e) {
                Log.w(TAG, "getSubscription:exception", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(EventData fetchedEvent) {
            hideLoading();

            if (fetchedEvent != null) {
                startStreaming(fetchedEvent);

            } else {
                Log.d(TAG, "subscriptions: null");
                Toast.makeText(HomeActivity.this, "Cant create event, you account had permission live stream ?? ", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class StartEventTask extends AsyncTask<String, Void, Void> {
        private ScheduledExecutorService mScheduleTaskExecutor;
        private String broadCastId;
        private String rtmpLink;
        private String streamId;

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Void doInBackground(String... params) {
            broadCastId = params[0];
            streamId = params[1];
            rtmpLink = params[2];
            mScheduleTaskExecutor = Executors.newSingleThreadScheduledExecutor();
            YouTube youTube = YouTubeNewSingleton.newInstance(AppPreferences.INSTANCE.getKeyString(Constants.KeyPreference.ACCOUNT_NAME), HomeActivity.this).getYoutube();

            mScheduleTaskExecutor.scheduleAtFixedRate(() -> {
                try {
                    if (streamId != null) {
                        YouTubeApi.checkStreamStatus(youTube, broadCastId, streamId, mScheduleTaskExecutor);
                    }
                } catch (IOException e) {
                    Log.e(TAG, null, e);
                }
            }, 0, 2, TimeUnit.SECONDS);

            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            hideLoading();
//            Intent intent = new Intent(getApplicationContext(), TeacherActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(Constants.IntentKey.ACCOUNT, mAccount);
//            intent.putExtras(bundle);
            AppPreferences.INSTANCE.setKeyBoolean(Constants.KeyPreference.IS_LIVESTREAMED, true);
            AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.RTMP_GOOGLE, rtmpLink);
            AppPreferences.INSTANCE.setKeyString(Constants.KeyPreference.BROADCAST_ID, broadCastId);
            Intent intent = new Intent(HomeActivity.this, TeacherActivity.class);
            startActivity(intent);
//            startActivity(intent);
        }

    }

    public void startStreaming(EventData event) {
        String broadcastId = event.getId();
        String streamId = event.getStreamId();
        new StartEventTask().execute(broadcastId, streamId, event.getIngestionAddress());
    }
}
