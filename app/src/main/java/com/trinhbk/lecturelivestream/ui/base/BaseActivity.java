package com.trinhbk.lecturelivestream.ui.base;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.teacher.TeacherActivity;
import com.trinhbk.lecturelivestream.widget.LoadingDialog;

public class BaseActivity extends AppCompatActivity {

    public static final long MIN_CLICK_INTERVAL = 1000L;

    private LoadingDialog dialog;
    private long mLastTimeClick = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new LoadingDialog(this, LoadingDialog.LOAD);
    }

    public void showLoading() {
        dialog.setMode(LoadingDialog.LOAD);
        dialog.setLoadingText(getString(R.string.dialog_loading));
        dialog.show();
    }

    public void hideLoading() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showErrorDialog(String message, String content, LoadingDialog.OnLiveClickListener listener) {
        dialog.setMode(LoadingDialog.ERROR);
        dialog.setText(message);
        dialog.setSubtext(content);
        dialog.setNegativeButton(false);
        dialog.setAffirmativeClickListener(listener);
        dialog.show();
    }

    public void showCautionDialog(String message, String content, LoadingDialog.OnLiveClickListener listener) {
        dialog.setMode(LoadingDialog.CAUTION);
        dialog.setText(message);
        dialog.setSubtext(content);
        dialog.setNegativeButton(false);
        dialog.setAffirmativeClickListener(listener);
        dialog.show();
    }

    public void showConfirmDialog(String message, String content, LoadingDialog.OnLiveClickListener listener) {
        dialog.setMode(LoadingDialog.CAUTION);
        dialog.setText(message);
        dialog.setSubtext(content);
        dialog.setAffirmativeClickListener(listener);
        dialog.setNegativeButton(true);
        dialog.setNegativeClickListener(liveDialog -> liveDialog.dismiss());
        dialog.show();
    }

    protected boolean avoidDoubleClick() {
        long currentTimeClick = System.currentTimeMillis();
        long elapsedTime = currentTimeClick - mLastTimeClick;
        mLastTimeClick = currentTimeClick;
        if (elapsedTime < MIN_CLICK_INTERVAL) {
            return true;
        }
        return false;
    }


}
