package org.hobart.facetrans.task.impl;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.model.VideoFolder;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
        LogcatUtils.d("VideoAsyncTask query cost time :" + (System.currentTimeMillis() - startTime) / 1000 + "秒");
        return videoFolders;
    }

    private static ArrayList<VideoFolder> loadLocalFolderContainsVideo() {

        ArrayList<VideoFolder> videoFolders = new ArrayList<>();

        String[] projection = {
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.SIZE
        };

        String where = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                "video/mkv", "video/mov", "video/mpg"};

        HashMap<String, List<Video>> videoMaps = new HashMap<>();

        Context context = FaceTransApplication.getFaceTransApplicationContext();

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"),
                projection, where, whereArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                if (size <= 10) continue;
                Video video = new Video();
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                video.setFilePath(path);
                video.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)));
                video.setSize(size);
                long dataAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
                video.setDateAdded(dataAdded);
                video.setFileType(FTType.VIDEO);
                if (path.contains("/tencent/MicroMsg/")) {
                    //认为是同个文件夹
                    if (!videoMaps.containsKey("微信")) {
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put("微信", videos);
                    } else {
                        videoMaps.get("微信").add(video);
                    }
                } else if (path.contains("/tencent/MobileQQ/")) {
                    if (!videoMaps.containsKey("QQ")) {
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put("QQ", videos);
                    } else {
                        videoMaps.get("QQ").add(video);
                    }
                } else if (path.contains("/com.qiyi.video/files/")) {
                    if (!videoMaps.containsKey("爱奇艺")) {
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put("爱奇艺", videos);
                    } else {
                        videoMaps.get("爱奇艺").add(video);
                    }
                } else {
                    String parentName = new File(path).getParentFile().getName();
                    if (!videoMaps.containsKey(parentName)) {
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put(parentName, videos);
                    } else {
                        videoMaps.get(parentName).add(video);
                    }
                }
            }
            cursor.close();
        }

        if (null != videoMaps && videoMaps.size() > 0) {
            Set<String> keys = videoMaps.keySet();
            for (String key : keys) {
                List<Video> videos = videoMaps.get(key);
                Collections.sort(videos, new Comparator<Video>() {
                    @Override
                    public int compare(Video o1, Video o2) {
                        return o1.getDateAdded() > o2.getDateAdded() ? 1 : 0;
                    }
                });
                final int size = videos.size();
                VideoFolder folder = new VideoFolder();
                folder.setVideos(videos);
                folder.setFolderName(key);
                folder.setFolderFileNum(size);
                folder.setFolderIconPath(videos.get(0).getFilePath());
                videoFolders.add(folder);
            }
        }
        return videoFolders;
    }
}
