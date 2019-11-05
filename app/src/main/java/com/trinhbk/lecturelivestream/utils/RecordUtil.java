package com.trinhbk.lecturelivestream.utils;

import android.os.Environment;
import android.util.Log;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecordUtil {
    private static final String TAG = RecordUtil.class.getSimpleName();
    public static final String baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath();
    private static RecordUtil instance;
    private String[] videos;

    public static RecordUtil getInstance() {
        if (instance == null) {
            instance = new RecordUtil();
        }
        return instance;
    }

    public void appendVideo(ArrayList<String> listRecordPaths, ArrayList<String> listRecordNames) {
        try {
            videos = listRecordPaths.toArray(new String[0]);
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
            StringBuilder videoCombinePath = new StringBuilder(baseDir + "/TONGHOP_");
            for(int i=0;i<listRecordNames.size();i++){
                if(i!=listRecordNames.size()-1){
                    videoCombinePath.append(listRecordNames.get(i)+"_");
                }else{
                    videoCombinePath.append(listRecordNames.get(i)+".mp4");
                }
            }
            Container out = new DefaultMp4Builder().build(result);
            FileChannel fc = new RandomAccessFile(videoCombinePath.toString(), "rw").getChannel();
            out.writeContainer(fc);
            fc.close();
            deleteFile(listRecordPaths);
            Log.v(TAG, "after combine videoCombinepath = " + videoCombinePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(ArrayList<String> listRecordPaths) {
        for(String listRecordPath:listRecordPaths){
            File fileDelete = new File(listRecordPath);
            if (fileDelete.exists()) {
                fileDelete.delete();
            }
        }
    }

}
