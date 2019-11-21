package com.trinhbk.lecturelivestream.utils;

import android.os.Environment;


public class Constants {

    public static class IntentKey {
        public static final String KEY_INTENT_USER_PERMISSION = "key_intent_user_permission";
        public static final String LECTURE = "lecture";
        public static final String EXTRA_DATA = "data";
        public static final String FROM_LOGIN = "isFromLogin";
        public static final String EXTRA_RESULT_CODE = "result_code";
        public static final String EXTRA_VIDEO_URL = "video_url";
        public static final String EXTRA_PREVIEW_TYPE = "preview_type";
        public static final String ACCOUNT = "account";

    }

//    public static final class file {
//        public static final String KEY_INTENT_PATH_DIR = "selected_folder";
//        public static final String STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//
//    }

    public static final class KeyPreference {
        public static final String IS_LOGINED = "isSignIn";
        public static final String IS_LIVESTREAMED = "isLiveStreamed";
        public static final String USER_ID = "userId";
        public static final String LOGIN_FROM_FACEBOOK = "isFacebookSignIn";
        public static final String RTMP_FACEBOOK = "rtmpFacebook";

        public static final String LOGIN_FROM_GOOGLE = "isGoogleSignIn";
        public static final String ACCOUNT_NAME = "accountName";
        public static final String BROADCAST_ID = "broadcastId";
        public static final String RTMP_GOOGLE = "rtmpGoogle";

    }

    public static final class RequestCode {

        public static final int REQUEST_ACCOUNT_PICKER = 200;
        public static final int REQUEST_LOGIN = 201;
        public static final int REQUEST_LOGIN_GOOGLE = 202;
        public static final int REQUEST_RECOVERY_ACCOUNT = 203;
        public static final int RC_SIGN_IN = 9001;

    }

    public static final class Action {
        //Action
        public static final int ACTION_START_RECORDING = 1;
        public static final int ACTION_STOP_RECORDING = 2;
        public static final int ACTION_PAUSE_RECORDING = 3;
        public static final int ACTION_RESUME_RECORDING = 4;
    }
}
