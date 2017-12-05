package org.hobart.facetrans.task.impl;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class ImageAsyncTask extends FTTask<List<ImageFolder>> {

    public ImageAsyncTask(FTTaskCallback<List<ImageFolder>> callback) {
        super(callback);
    }

    @Override
    protected List<ImageFolder> execute() {

        List<ImageFolder> folders = loadLocalFolderContainsImage();

        if (null != folders && folders.size() > 0) {
            for (ImageFolder folder : folders) {
                folder.setImages(queryFolderPictures(new File(folder.getFirstFilePath()).getParentFile().getAbsolutePath()));
            }
        }
        return folders;
    }

    static ArrayList<ImageFolder> loadLocalFolderContainsImage() {

        ArrayList<ImageFolder> imageFolders = new ArrayList<>();

        Context context = FaceTransApplication.getFaceTransApplicationContext();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[]{
                        "COUNT(" + MediaStore.Files.FileColumns.PARENT + ") AS fileCount",
                        MediaStore.Files.FileColumns.DATA + " FROM (SELECT *",
                },
                MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + ")"
                        + " ORDER BY " + MediaStore.Files.FileColumns.DATE_MODIFIED + " )"
                        + " GROUP BY (" + MediaStore.Files.FileColumns.PARENT,
                null,
                "fileCount DESC"
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ImageFolder imageFolder = new ImageFolder();
                int imageFileCountInFolder = cursor.getInt(0);
                String latestImageFilePath = cursor.getString(1);
                File folderFile = new File(latestImageFilePath).getParentFile();

                imageFolder.setFirstFilePath(latestImageFilePath);
                imageFolder.setFolderFileNum(imageFileCountInFolder);
                imageFolder.setFolderName(folderFile.getName());

                imageFolders.add(imageFolder);
            }
            cursor.close();
        }
        return imageFolders;
    }

    private static ArrayList<Image> queryFolderPictures(final String folderPath) {

        ArrayList<Image> list = new ArrayList<>();

        String[] columns = new String[]{MediaStore.Images.Media.DATA};

        String whereclause = MediaStore.Images.ImageColumns.DATA + " like'" + folderPath + "/%'";

        Cursor corsor = null;

        Context context = FaceTransApplication.getFaceTransApplicationContext();

        try {
            corsor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, whereclause, null,
                    null);
            if (corsor != null && corsor.getCount() > 0 && corsor.moveToFirst()) {
                do {
                    String path = corsor.getString(corsor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                    Image image = new Image();
                    long size = 0;
                    try {
                        File file = new File(path);
                        size = file.length();
                        image.setSize(size);
                    } catch (Exception e) {
                    }
                    image.setFilePath(path);
                    image.setFileType(FTType.IMAGE);
                    list.add(0, image);
                } while (corsor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (corsor != null)
                corsor.close();
        }
        return list;
    }

}
