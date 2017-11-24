package org.hobart.facetrans.task.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class ImageAsyncTask extends FTTask<List<Image>> {

    public ImageAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Image> execute() {
        return queryImage();
    }

    private List<Image> queryImage() {
        final Context context = FaceTransApplication.getFaceTransApplicationContext().getApplicationContext();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA}, null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        if (null == cursor) {
            return null;
        }
        List<Image> images = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Image image = new Image();
                image.setSize(cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
                image.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.TITLE)));
                image.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                images.add(image);
            }
        } finally {
            cursor.close();
        }
        return images;
    }
}
