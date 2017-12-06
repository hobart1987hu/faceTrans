package org.hobart.facetrans.task.impl;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.model.Apk;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                apk.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                apk.setBitmap(drawableToBitmap(packageInfo.applicationInfo.loadIcon(packageManager)));
                String path = packageInfo.applicationInfo.sourceDir;
                apk.setFilePath(path);
                apk.setVersionName(packageInfo.versionName);
                apk.setSize(new File(path).length());
                apk.setSizeDesc(FileUtils.getFileSize(apk.getSize()));
                apk.setFileType(FTType.APK);
                appList.add(apk);
            }
        }
        return appList;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
