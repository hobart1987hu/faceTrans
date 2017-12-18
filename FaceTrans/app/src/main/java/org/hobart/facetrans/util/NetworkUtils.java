package org.hobart.facetrans.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.hobart.facetrans.FaceTransApplication;

/**
 * Created by huzeyin on 2017/12/15.
 */

public class NetworkUtils {

    public static boolean isGPRSAvailable() {
        Context context = FaceTransApplication.getFaceTransApplicationContext();

        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo && networkInfo.isAvailable()) {
            return true;
        }
        return false;
    }
}
