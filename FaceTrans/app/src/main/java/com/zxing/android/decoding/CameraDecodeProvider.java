package com.zxing.android.decoding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.zxing.android.camera.CameraManager;
import com.zxing.android.view.ViewfinderView;

/**
 * Created by huzeyin on 2017/11/14.
 */

public interface CameraDecodeProvider {

    CameraManager getCameraManager();

    Handler getHandler();

    ViewfinderView getViewfinderView();

    void handleDecode(Result result, Bitmap barcode);

    Activity getView();
}
