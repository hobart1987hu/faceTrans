package org.hobart.facetrans.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import org.hobart.facetrans.FaceTransApplication;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class AndroidUtils {

    public static int getAndroidVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static void requestWriteSettings(Activity activity, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 根据手机的分辨率从 dp的单位转成为 px(像素)
     *
     * @param dpValue
     * @return
     */
    public static int dip2px(float dpValue) {
        final float scale = FaceTransApplication.getFaceTransApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
