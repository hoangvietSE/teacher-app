package com.trinhbk.lecturelivestream.utils;

import android.widget.Toast;

import com.trinhbk.lecturelivestream.application.MainApplication;

public class ToastUtil {
    private ToastUtil() {
    }

    private static ToastUtil instance;

    public static ToastUtil getInstance() {
        if (instance == null) {
            instance = new ToastUtil();
        }
        return instance;
    }

    public void show(String msg){
        Toast.makeText(MainApplication.instance, msg, Toast.LENGTH_LONG).show();
    }
}
