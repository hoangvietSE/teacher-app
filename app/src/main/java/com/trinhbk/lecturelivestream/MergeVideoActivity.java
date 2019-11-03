package com.trinhbk.lecturelivestream;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.trinhbk.lecturelivestream.ui.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MergeVideoActivity extends BaseActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 1998;
    private static final String TAG = MergeVideoActivity.class.getSimpleName();
    private FFmpeg fFmpeg;
    public ArrayList<String> filePaths;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePaths = new ArrayList<>();
        filePaths.add(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + File.separator + "46.mp4").toString());
        filePaths.add(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + File.separator + "56.mp4").toString());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    REQUEST_CAMERA_PERMISSION);
            return;
        }
        loadFFmpegLibrary();
        String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
        String fileName = "46.mp4";
        File f = new File(baseDir + File.separator + fileName);
        String video1 = baseDir + "/vandam.mp4";
        String video2 = baseDir + "/doahoahong.mp4";
//        String video3 = baseDir + "/BeSureToWear.mp4";
        String[] videos = new String[]{video1, video2};
        try {
            showLoading();
            appendVideo(videos);
            hideLoading();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFFmpegLibrary() {
        if (fFmpeg == null) {
            fFmpeg = FFmpeg.getInstance(this);
            try {
                fFmpeg.loadBinary(new FFmpegLoadBinaryResponseHandler() {
                    @Override
                    public void onFailure() {
                        Log.d(TAG, "LoadLibrary: onFailure");
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "LoadLibrary: onSuccess");
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
            } catch (FFmpegNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeCommand(final String[] command) {
        try {
            fFmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                }

                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private String appendVideo(String[] videos) throws IOException {
        Log.v(TAG, "in appendVideo() videos length is " + videos.length);
        Movie[] inMovies = new Movie[videos.length];
        int index = 0;
        for (String video : videos) {
            Log.i(TAG, "    in appendVideo one video path = " + video);
            inMovies[index] = MovieCreator.build(video);
            index++;
        }
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();
        Log.v(TAG, "audioTracks size = " + audioTracks.size()
                + " videoTracks size = " + videoTracks.size());
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }
        String videoCombinePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/testphatcuoi.mp4";
        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(videoCombinePath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
        Log.v(TAG, "after combine videoCombinepath = " + videoCombinePath);
        return videoCombinePath;
    }
}
