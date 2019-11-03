package com.trinhbk.lecturelivestream.ui.trimmer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.utils.Constants;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

/**
 * Created by TrinhBK on 9/21/2018.
 */

public class EditVideoActivity extends BaseActivity {

    private K4LVideoTrimmer videoTrimmer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_video);
        videoTrimmer = findViewById(R.id.timeLine);

        if (videoTrimmer != null) {
            videoTrimmer.setMaxDuration(10000000);
            videoTrimmer.setDestinationPath(getIntent().getStringExtra(Constants.IntentKey.EXTRA_VIDEO_URL));
            videoTrimmer.setVideoURI(Uri.parse(getIntent().getStringExtra(Constants.IntentKey.EXTRA_VIDEO_URL)));
            videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
                @Override
                public void getResult(Uri uri) {
                    finish();
                }

                @Override
                public void cancelAction() {
                    finish();
                }
            });
        }
    }
}
