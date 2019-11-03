package com.trinhbk.lecturelivestream.ui.clip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.trinhbk.lecturelivestream.R;
import com.trinhbk.lecturelivestream.adapter.VideoAdapter;
import com.trinhbk.lecturelivestream.model.Video;
import com.trinhbk.lecturelivestream.ui.BaseActivity;
import com.trinhbk.lecturelivestream.ui.teacher.TeacherActivity;
import com.trinhbk.lecturelivestream.ui.trimmer.EditVideoActivity;
import com.trinhbk.lecturelivestream.utils.Constants;
import com.trinhbk.lecturelivestream.widget.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TrinhBK on 10/3/2018.
 */

public class ListVideoActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, VideoAdapter.OnClickVideo {

    public static final String TAG = ListVideoActivity.class.getSimpleName();

    private Toolbar tbVideo;
    private FloatingActionButton fabCall;
    private SwipeRefreshLayout srlVideo;
    private RecyclerView rvVideo;

    private VideoAdapter homeAdapter;
    private List<Video> videos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        initView();
        initEvent();
    }


    private void initView() {
        tbVideo = findViewById(R.id.tbVideo);
        srlVideo = findViewById(R.id.srlVideos);
        rvVideo = findViewById(R.id.rvVideos);
        fabCall = findViewById(R.id.fabHome);
        videos = new ArrayList<>();

        setSupportActionBar(tbVideo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.tv_title_action_bar);

        RecyclerView.LayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvVideo.setLayoutManager(recyclerViewLayoutManager);
        rvVideo.addItemDecoration(new DividerItemDecoration(this));
        homeAdapter = new VideoAdapter(videos, this);
        rvVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fabCall.getVisibility() == View.VISIBLE) {
                    fabCall.hide();
                } else if (dy < 0 && fabCall.getVisibility() != View.VISIBLE) {
                    fabCall.show();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        fabCall.setEnabled(true);
        hideLoading();
        fetchVideo();
    }

    private void initEvent() {
        rvVideo.setAdapter(homeAdapter);
        fabCall.setOnClickListener(this);
        srlVideo.setOnRefreshListener(this);
    }

    private void fetchVideo() {
        videos.clear();
        srlVideo.setRefreshing(false);
        searchVid(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        if (videos.size() > 0) {
            Collections.sort(videos, (video, t1) -> {
                long k = video.getVideoDate() - t1.getVideoDate();
                if (k < 0) {
                    return 1;
                } else if (k == 0) {
                    return 0;
                } else {
                    return -1;
                }
            });
            homeAdapter.notifyDataSetChanged();
        }
    }

    public void searchVid(File dir) {
        String pattern = ".mp4";
        //Get the listfile of that flder
        File listFile[] = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
//                final int x = i;
                if (listFile[i].isDirectory()) {
//                    walkdir(listFile[i]);
                } else {
                    if (listFile[i].getName().endsWith(pattern)) {
                        // Do what ever u want, add the path of the video to the list
//                        pathVideos.add(listFile[i].getAbsolutePath());
                        Video video = new Video(listFile[i].getName(), listFile[i].getAbsolutePath(), listFile[i].lastModified());
                        videos.add(video);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabHome:
                call();
                break;
        }
    }

    private void call() {
        fabCall.setEnabled(false);
        Intent intent = new Intent(ListVideoActivity.this, TeacherActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onItemWatchVideo(int position) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videos.get(position).getVideoPath()));
        intent.setDataAndType(Uri.parse(videos.get(position).getVideoPath()), "video/mp4");
        startActivity(intent);
    }

    @Override
    public void onItemCropVideo(int position) {
        Intent intent = new Intent(ListVideoActivity.this, EditVideoActivity.class);
        intent.putExtra(Constants.IntentKey.EXTRA_VIDEO_URL, videos.get(position).getVideoPath());
        startActivity(intent);
    }

    @Override
    public void onItemShareVideo(int position) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        File fileToShare = new File(videos.get(position).getVideoPath());
        Uri uri = Uri.fromFile(fileToShare);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share Video!"));
    }

    @Override
    public void onItemDeleteVideo(int position) {
        showConfirmDialog("Bạn muốn xóa tập tin " + videos.get(position).getVideoName() + " khỏi hệ thống?", "", liveDialog -> {
            File fileDelete = new File(videos.get(position).getVideoPath());
            if (fileDelete.exists()) {
                fileDelete.delete();
            }
            videos.remove(position);
            homeAdapter.notifyDataSetChanged();
            liveDialog.dismiss();
        });
    }

    @Override
    public void onRefresh() {
        fetchVideo();
    }
}
