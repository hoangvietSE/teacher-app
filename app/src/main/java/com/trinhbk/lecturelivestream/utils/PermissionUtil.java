package com.trinhbk.lecturelivestream.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.trinhbk.lecturelivestream.MainApplication;

public class PermissionUtil {
    private PermissionUtil() {
    }

    private static PermissionUtil instance;

    public static PermissionUtil getInstance() {
        if (instance == null) {
            instance = new PermissionUtil();
        }
        return instance;
    }

    public boolean hasPermission(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(MainApplication.instance, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void reuqestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public void goToSettingPermission(Activity activity) {
        Intent settingPermissionIntent = new Intent();
        settingPermissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        settingPermissionIntent.setData(uri);
        activity.startActivity(settingPermissionIntent);

    }
}
