package test;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.model.VideoFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huzeyin on 2017/12/8.
 */

public class VideoListTask {

    public static HashMap<String, List<Video>> loadLocalFolderContainsVideo() {

        long startTime = System.currentTimeMillis();

        Log.d("hulaoda", "loadLocalFolderContainsVideo start time " + startTime);

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
                Video video = new Video();
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)); // 大小
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                video.setFilePath(path);
                video.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)));
                video.setSize(size);
                final String tempPath = path;
                if (tempPath.contains("/tencent/MicroMsg/")) {
                    //认为是同个文件夹
                    video.setName("微信");
                    if (!videoMaps.containsKey("微信")) {
                        Log.d("hulaoda", "微信 add");
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put("微信", videos);
                    } else {
                        Log.d("hulaoda", "微信 put");
                        videoMaps.get("微信").add(video);
                    }
                } else if (tempPath.contains("/tencent/MobileQQ/")) {
                    video.setName("QQ");
                    if (!videoMaps.containsKey("QQ")) {
                        Log.d("hulaoda", "QQ put");
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put("QQ", videos);
                    } else {
                        Log.d("hulaoda", "QQ add");
                        videoMaps.get("QQ").add(video);
                    }
                } else if (tempPath.contains("/com.qiyi.video/files/")) {
                    video.setName("爱奇艺");
                    if (!videoMaps.containsKey("爱奇艺")) {
                        Log.d("hulaoda", "爱奇艺 add");
                        List<Video> videos = new ArrayList<>();
                        videos.add(video);
                        videoMaps.put("爱奇艺", videos);
                    } else {
                        Log.d("hulaoda", "爱奇艺 put");
                        videoMaps.get("爱奇艺").add(video);
                    }
                } else {
                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    //这边只做分组
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
        Log.d("hulaoda", "loadLocalFolderContainsVideo cost  time " + (System.currentTimeMillis() - startTime) / 1000 + "秒");
        return videoMaps;
    }
}
