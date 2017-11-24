package org.hobart.facetrans.task.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class MusicAsyncTask extends FTTask<List<Music>> {

    public MusicAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Music> execute() {
        return queryMusic();
    }

    private List<Music> queryMusic() {
        final Context context = FaceTransApplication.getFaceTransApplicationContext().getApplicationContext();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA}, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (null == cursor) {
            return null;
        }
        List<Music> musics = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.setMusicName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                music.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
                music.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
                music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                musics.add(music);
            }
        } finally {
            cursor.close();
        }
        return musics;
    }

}
