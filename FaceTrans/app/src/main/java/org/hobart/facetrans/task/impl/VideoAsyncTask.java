package org.hobart.facetrans.task.impl;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.R;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.model.VideoFolder;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ScreenshotUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class VideoAsyncTask extends FTTask<List<VideoFolder>> {

    public VideoAsyncTask(FTTaskCallback<List<VideoFolder>> callback) {
        super(callback);
    }

    @Override
    protected List<VideoFolder> execute() {
        long startTime = System.currentTimeMillis();
        LogcatUtils.d("VideoAsyncTask query start time :" + startTime);
        List<VideoFolder> videoFolders = loadLocalFolderContainsVideo();
//        if (null != videoFolders && videoFolders.size() > 0) {
//            for (VideoFolder folder : videoFolders) {
//                folder.setVideos(queryFolderVideos(folder.getFolderPath()));
//            }
//        }
        LogcatUtils.d("VideoAsyncTask query cost time :" + (System.currentTimeMillis() - startTime) / 1000 + "秒");
        return videoFolders;
    }

    /***
     * ".mp4", ".3gp", ".wmv", ".ts", ".rmvb", ".mov", ".m4v", ".avi", ".m3u8", ".3gpp", ".3gpp2", ".mkv"
     , ".flv", ".divx", ".divx", ".rm", ".asf", ".ram", ".mpg", ".v8", ".swf", ".m2v", ".asx", ".ra", ".ndivx", ".xvid"
     * */

    private static final String[] EXTENSION = {".mp4", ".rmvb", ".wmv", ".flv"};

    public static ArrayList<VideoFolder> loadLocalFolderContainsVideo() {

        String selection = "";
        for (int i = 0; i < EXTENSION.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + EXTENSION[i] + "'";
        }

        ArrayList<VideoFolder> videoFolders = new ArrayList<>();

        Context context = FaceTransApplication.getFaceTransApplicationContext();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[]{
                        "COUNT(" + MediaStore.Files.FileColumns.PARENT + ") AS fileCount",
                        MediaStore.Files.FileColumns.DATA + " FROM (SELECT *",
                },
                selection + ")"
                        + " ORDER BY " + MediaStore.Files.FileColumns.DATE_MODIFIED + " )"
                        + " GROUP BY (" + MediaStore.Files.FileColumns.PARENT,
                null,
                "fileCount DESC"
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {

                VideoFolder videoFolder = new VideoFolder();

                int videoFileCountInFolder = cursor.getInt(0);

                String latestVideoFilePath = cursor.getString(1);

                File folderFile = new File(latestVideoFilePath).getParentFile();

                videoFolder.setFolderFileNum(videoFileCountInFolder);

                videoFolder.setFolderName(folderFile.getName());

                videoFolder.setFolderPath(folderFile.getAbsolutePath());

                Bitmap bitmap = null;
                try {
                    bitmap = ScreenshotUtils.createVideoThumbnail(latestVideoFilePath);
                } catch (Exception e) {
                    bitmap = BitmapFactory.decodeResource(FaceTransApplication.getApp().getResources(), R.mipmap.icon_default);
                }
                videoFolder.setFirstVideoBitmap(bitmap);
                videoFolders.add(videoFolder);

            }
            cursor.close();
        }
        return videoFolders;
    }

    /**
     * 获取文件夹下所有的视频文件
     *
     * @param folderPath
     * @return
     */
    public static ArrayList<Video> queryFolderVideos(final String folderPath) {

        long startTime = System.currentTimeMillis();
        LogcatUtils.d("VideoAsyncTask queryFolderVideos  query start time :" + startTime);

        ArrayList<Video> list = new ArrayList<>();

        Uri fileUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{
                MediaStore.Video.VideoColumns.DATA,
        };

        String selection = "";
        for (int i = 0; i < EXTENSION.length; i++) {
            if (i != 0) {
                selection = selection + " OR ";
            }
            selection = selection + MediaStore.Files.FileColumns.DATA + " LIKE '%" + EXTENSION[i] + "'";
        }

        String whereclause = MediaStore.Video.VideoColumns.DATA + " like'" + folderPath + "/%' and " + selection;


        String sortOrder = MediaStore.Video.VideoColumns.DATE_MODIFIED;

        Cursor cursor = FaceTransApplication.getApp().getContentResolver().query(fileUri, projection, whereclause, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String path = cursor.getString(0);
                    Video video = new Video();
                    video.setFilePath(path);
                    long size = 0;
                    try {
                        File file = new File(path);
                        size = file.length();
                        video.setSize(size);
                    } catch (Exception e) {

                    }
                    video.setFileType(FTType.VIDEO);

                    Bitmap bitmap = null;
                    try {
                        bitmap = ScreenshotUtils.createVideoThumbnail(path);
                    } catch (Exception e) {
                        bitmap = BitmapFactory.decodeResource(FaceTransApplication.getApp().getResources(), R.mipmap.icon_default);
                    }
                    video.setBitmap(bitmap);
                    list.add(0, video);

                } catch (Exception e) {
                }
            }
        }

        LogcatUtils.d("VideoAsyncTask queryFolderVideos  query cost  time :" + (System.currentTimeMillis() - startTime) / 1000 + "秒");

        return list;
    }
}
