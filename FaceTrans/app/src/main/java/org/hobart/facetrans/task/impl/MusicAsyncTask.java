package org.hobart.facetrans.task.impl;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.FTFile;
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
        List<Music> musics = getSpecificTypeFiles();
        musics = getDetailFTFiles(musics);
        return musics;
    }

    private static List<Music> getSpecificTypeFiles() {

        List<Music> musics = new ArrayList<>();

        Uri fileUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.PARENT,
        };

        String selection = MediaStore.Files.FileColumns.DATA + " LIKE '%" + ".mp3" + "'";

        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

        Cursor cursor = FaceTransApplication.getApp().getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(0);
                    Music music = new Music();
                    music.setFilePath(data);
                    long size = 0;
                    try {
                        File file = new File(data);
                        size = file.length();
                        music.setSize(size);
                    } catch (Exception e) {

                    }
                    musics.add(music);
                } catch (Exception e) {
                }
            }
        }
        return musics;
    }

    static List<Music> getDetailFTFiles(List<Music> fileInfoList) {

        if (fileInfoList == null || fileInfoList.size() <= 0) {
            return fileInfoList;
        }
        for (Music music : fileInfoList) {
            if (music != null) {
                music.setName(FileUtils.getFileName(music.getFilePath()));
                music.setSizeDesc(FileUtils.getFileSize(music.getSize()));
                music.setFileType(FTType.MUSIC);
            }
        }
        return fileInfoList;
    }

}
