package com.trinhbk.lecturelivestream.model;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by TrinhBK on 9/21/2018.
 */

public class Video {

    private String videoName;
    private String videoThumb;
    private long videoDate;
    private String videoPath;

    public Video(String videoName, String videoPath) {
        this.videoName = videoName;
        this.videoPath = videoPath;
    }

    public Video(String videoName, String videoPath, long videoDate) {
        this.videoName = videoName;
        this.videoPath = videoPath;
        this.videoDate = videoDate;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public long getVideoDate() {
        return videoDate;
    }

    public void setVideoDate(long videoDate) {
        this.videoDate = videoDate;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
