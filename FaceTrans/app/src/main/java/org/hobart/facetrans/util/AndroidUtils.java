package org.hobart.facetrans.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.hobart.facetrans.FaceTransApplication;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class AndroidUtils {

    public static String getPhoneModel() {
        return Build.MODEL;
    }

    public static String getCurrentApkPath() {

        return FaceTransApplication.getFaceTransApplicationContext().getApplicationInfo().sourceDir;
    }

    public static String getApkPkgName(String apkPath) {
        Context context = FaceTransApplication.getFaceTransApplicationContext();

        PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        String pkgName = null;
        if (pi != null) {
            pkgName = pi.packageName;
        }
        return pkgName;
    }

    public static String getVersionName(String apkPath) {

        Context context = FaceTransApplication.getFaceTransApplicationContext();

        PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        String versionName = null;
        if (pi != null) {
            versionName = pi.versionName;
        }
        return versionName;
    }

    public static Drawable getApkIcon(String apkPath) {

        Context context = FaceTransApplication.getFaceTransApplicationContext();

        PackageInfo pi = context.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (pi != null) {
            ApplicationInfo appInfo = pi.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(context.getPackageManager());
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap loadBitmapFromView(View comBitmap, int width, int height) {
        Bitmap bitmap = null;
        if (comBitmap != null) {
            comBitmap.clearFocus();
            comBitmap.setPressed(false);

            boolean willNotCache = comBitmap.willNotCacheDrawing();
            comBitmap.setWillNotCacheDrawing(false);

            // Reset the drawing cache background color to fully transparent
            // for the duration of this operation
            int color = comBitmap.getDrawingCacheBackgroundColor();
            comBitmap.setDrawingCacheBackgroundColor(0);
            float alpha = comBitmap.getAlpha();
            comBitmap.setAlpha(1.0f);

            if (color != 0) {
                comBitmap.destroyDrawingCache();
            }
            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            comBitmap.measure(widthSpec, heightSpec);
            comBitmap.layout(0, 0, width, height);
            comBitmap.buildDrawingCache();
            Bitmap cacheBitmap = comBitmap.getDrawingCache();
            if (cacheBitmap == null) {
                return null;
            }
            bitmap = Bitmap.createBitmap(cacheBitmap);
            // Restore the view
            comBitmap.setAlpha(alpha);
            comBitmap.destroyDrawingCache();
            comBitmap.setWillNotCacheDrawing(willNotCache);
            comBitmap.setDrawingCacheBackgroundColor(color);
        }
        return bitmap;
    }


    public static void requestWriteSettings(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

    public static int dip2px(float dpValue) {
        final float scale = FaceTransApplication.getFaceTransApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setStatusBarAndBottomBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    private static String imei = "";
    private static String mac;

    public static String getDeviceId() {
        try {
            if (TextUtils.isEmpty(imei)) {
                TelephonyManager tm = (TelephonyManager) FaceTransApplication.getApp().getSystemService(Context.TELEPHONY_SERVICE);
                imei = tm.getDeviceId();
                if (TextUtils.isEmpty(imei) || imei.equals("0"))
                    imei = getMacAddress().replaceAll(":", "_");
            }
        } catch (Exception e) {
            imei = getMacAddress().replaceAll(":", "_");
        }
        return imei;
    }


    private static String getMacAddress() {
        String result = "";
        try {
            if (!TextUtils.isEmpty(mac)) return mac;
            WifiManager wifiManager = (WifiManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            result = wifiInfo.getMacAddress();
            mac = result;
            if (mac == null || mac.equals("")) mac = getIDFinal();
        } catch (Exception e) {
            if (mac == null || mac.equals("")) mac = getIDFinal();
        }
        return result;
    }

    private static String getIDFinal() {
        String m_szDevIDShort = "35";
        try {
            m_szDevIDShort = "35" + //we make this look like a valid IMEI
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 +
                    Build.HOST.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 +
                    Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m_szDevIDShort;
    }
}
