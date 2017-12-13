package org.hobart.facetrans.model;

import android.graphics.Bitmap;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class Music extends FTFile {

    private Bitmap thumbnail;

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
