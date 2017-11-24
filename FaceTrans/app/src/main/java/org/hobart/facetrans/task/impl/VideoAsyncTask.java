package org.hobart.facetrans.task.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class VideoAsyncTask extends FTTask<List<Video>> {

    public VideoAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Video> execute() {
        return queryVideo();
    }

    private List<Video> queryVideo() {
        final Context context = FaceTransApplication.getFaceTransApplicationContext().getApplicationContext();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA}, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        if (null == cursor) {
            return null;
        }
        List<Video> musics = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Video video = new Video();
                video.setVideoName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                video.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                video.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
                video.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                musics.add(video);
            }
        } finally {
            cursor.close();
        }
        return musics;
    }
}
