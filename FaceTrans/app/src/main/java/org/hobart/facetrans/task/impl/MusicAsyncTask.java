package org.hobart.facetrans.task.impl;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class MusicAsyncTask extends FTTask<List<Music>> {

    public MusicAsyncTask(FTTaskCallback<List<Music>> callback) {
        super(callback);
    }

    @Override
    protected List<Music> execute() {
        return getSpecificTypeFiles();
    }

    /**
     * @return
     */
    private static List<Music> getSpecificTypeFiles() {

        List<Music> musics = new ArrayList<>();

        Uri fileUri = MediaStore.Files.getContentUri("external");

        String[] projection = new String[]{MediaStore.Files.FileColumns.DATA};

        String selection = MediaStore.Files.FileColumns.DATA + " LIKE '%" + ".mp3" + "'";

        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

        Cursor cursor = FaceTransApplication.getApp().getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String path = cursor.getString(0);
                    Music music = new Music();
                    music.setFilePath(path);
                    long size = 0;
                    try {
                        File file = new File(path);
                        size = file.length();
                        if (size <= 0) continue;
                        music.setSize(size);
                    } catch (Exception e) {

                    }
                    music.setName(FileUtils.getFileName(music.getFilePath()));
                    music.setSizeDesc(FileUtils.getFileSize(music.getSize()));
                    music.setFileType(FTType.MUSIC);
                    musics.add(music);
                } catch (Exception e) {
                }
            }
        }
        return musics;
    }
}
