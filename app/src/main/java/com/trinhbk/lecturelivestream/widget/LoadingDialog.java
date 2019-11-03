package com.trinhbk.lecturelivestream.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trinhbk.lecturelivestream.R;


/**
 * Created by TrinhBK on 12/10/2018.
 */

public class LoadingDialog extends Dialog implements View.OnClickListener, Dialog.OnKeyListener {

    public static int LOAD = -1;

    public static int SUCCESS = 0;

    public static int ERROR = 1;

    public static int CAUTION = 2;

    private OnLiveClickListener listenerAffirmativeClick;

    private OnLiveClickListener listenerNegativeClick;

    private ProgressBar pbLoading;
    private LinearLayout llDialog;
    private TextView textLoading;
    private ImageView ivIcon;
    private TextView tvText;
    private TextView tvSubtext;
    private Button btnAffirmative;
    private Button btnNegative;

    private View content;

    private int mode;

    public interface OnLiveClickListener {
        void onClick(LoadingDialog liveDialog);
    }

    public LoadingDialog(Context context, int mode) {
        super(context);
        initView(context);
        initMode(mode);
        initListeners();
    }

    private void initView(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        content = View.inflate(context, R.layout.widget_dialog, null);
        setContentView(content);

        pbLoading = findViewById(R.id.pbLoading);
        llDialog = findViewById(R.id.llDialog);
        textLoading = findViewById(R.id.textLoading);
        ivIcon = findViewById(R.id.ivIcon);
        tvText = findViewById(R.id.tvText);
        tvSubtext = findViewById(R.id.tvSubtext);
        btnAffirmative = findViewById(R.id.btnAffirmative);
        btnNegative = findViewById(R.id.btnNegative);

        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        width = (int) (width - (width * 0.12f));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);

        setOnKeyListener(this);
    }

    private void initMode(int mode) {
        setMode(mode);
    }

    private void initListeners() {
        btnAffirmative.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAffirmative:
                if (listenerAffirmativeClick != null) listenerAffirmativeClick.onClick(this);
                else dismiss();
                break;
            case R.id.btnNegative:
                if (listenerNegativeClick != null) listenerNegativeClick.onClick(this);
                else dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mode != LOAD) dismiss();
        }
        return true;
    }

    public void setMode(int mode) {
        this.mode = mode;

        content.post(() -> {
            switch (mode) {
                case -1:
                    setCanceledOnTouchOutside(false);
                    pbLoading.setVisibility(View.VISIBLE);
                    llDialog.setVisibility(View.GONE);
                    textLoading.setVisibility(View.VISIBLE);
                    break;
                case 0:
                    setCanceledOnTouchOutside(true);
                    pbLoading.setVisibility(View.GONE);
                    llDialog.setVisibility(View.VISIBLE);
                    textLoading.setVisibility(View.GONE);
                    ivIcon.setImageResource(R.drawable.ic_circle_check);
                    break;
                case 1:
                    setCanceledOnTouchOutside(true);
                    pbLoading.setVisibility(View.GONE);
                    llDialog.setVisibility(View.VISIBLE);
                    textLoading.setVisibility(View.GONE);
                    ivIcon.setImageResource(R.drawable.ic_circle_error);
                    break;
                case 2:
                    setCanceledOnTouchOutside(true);
                    pbLoading.setVisibility(View.GONE);
                    llDialog.setVisibility(View.VISIBLE);
                    textLoading.setVisibility(View.GONE);
                    ivIcon.setImageResource(R.drawable.ic_circle_caution);
                    break;
            }
        });
    }

    public void setLoadingText(String text) {
        content.post(() -> textLoading.setText(text));
    }

    public void setText(String text) {
        content.post(() -> tvText.setText(text));
    }

    public void setSubtext(String text) {
        content.post(() -> {
            tvSubtext.setText(text);
            tvSubtext.setVisibility(text.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    public void setNegativeButton(boolean visibility) {
        content.post(() -> btnNegative.setVisibility(visibility ? View.VISIBLE : View.GONE));
    }

    public void setAffirmativeButtonText(String text) {
        content.post(() -> btnAffirmative.setText(text));
    }

    public void setNegativeButtonText(String text) {
        content.post(() -> btnNegative.setText(text));
    }

    public LoadingDialog setAffirmativeClickListener(OnLiveClickListener listener) {
        listenerAffirmativeClick = listener;
        return this;
    }

    public LoadingDialog setNegativeClickListener(OnLiveClickListener listener) {
        listenerNegativeClick = listener;
        return this;
    }

}
