package org.hobart.facetrans.task.impl;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.model.Apk;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.model.VideoFolder;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.FileUtils;

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

public class ApkAsyncTask extends FTTask<List<Apk>> {

    public ApkAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Apk> execute() {

        List<Apk> appList = new ArrayList<>();

        Context context = FaceTransApplication.getApp();

        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> packages = packageManager.getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                Apk apk = new Apk();
                String path = packageInfo.applicationInfo.sourceDir;
                apk.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                apk.setDrawable(packageInfo.applicationInfo.loadIcon(packageManager));
                apk.setFilePath(path);
                apk.setVersionName(packageInfo.versionName);
                apk.setSize(new File(path).length());
                apk.setSizeDesc(FileUtils.getFileSize(apk.getSize()));
                apk.setFileType(FTType.APK);
                appList.add(apk);
            }
        }
        appList.addAll(getSdcardApkList());
        return appList;
    }

    private static ArrayList<Apk> getSdcardApkList() {

        ArrayList<Apk> apks = new ArrayList<>();

        Uri fileUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{
                MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.TITLE
        };
        String selection = MediaStore.Files.FileColumns.DATA + " LIKE '%" + ".apk" + "'";

        Context context = FaceTransApplication.getApp();

        String sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED;

        Cursor cursor = context.getContentResolver().query(fileUri, projection, selection, null, sortOrder);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String path = cursor.getString(0);

                    long size = 0;
                    try {
                        File file = new File(path);
                        size = file.length();

                    } catch (Exception e) {

                    }
                    if (size <= 0) continue;

                    Apk apk = new Apk();

                    apk.setSize(size);

                    apk.setFilePath(path);


                    apk.setFileType(FTType.APK);

                    apk.setSizeDesc(FileUtils.getFileSize(size));

                    apk.setName(FileUtils.getFileName(path));

                    apk.setDrawable(AndroidUtils.getApkIcon(path));

                    apk.setVersionName(AndroidUtils.getVersionName(path));

                    apks.add(apk);

                } catch (Exception e) {
                }
            }
        }
        return apks;
    }
}
