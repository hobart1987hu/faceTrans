package org.hobart.facetrans.util;

import android.widget.Toast;

import org.hobart.facetrans.FaceTransApplication;

/**
 * Created by huzeyin on 2017/6/5.
 */

public class ToastUtils {


    public static void showLongToast(String info) {
        Toast.makeText(FaceTransApplication.getFaceTransApplicationContext(), info, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(String info) {
        Toast.makeText(FaceTransApplication.getFaceTransApplicationContext(), info, Toast.LENGTH_SHORT).show();
    }
}
