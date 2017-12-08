package org.hobart.facetrans.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class Apk extends FTFile {

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * 当前版本号
     */
    private String versionName;

    /**
     * Apk图标
     */
    private Drawable drawable;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    /**
     * APK图标
     */
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
