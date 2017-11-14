package org.hobart.facetrans.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.util.LogcatUtils;

import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * wifi helper class
 * Created by huzeyin on 2017/11/7.
 */

public class WifiHelper {

    private static final String TAG = "WifiHelper";

    private static WifiHelper mInstance = null;


    private static final ReentrantLock LOCK = new ReentrantLock();


    public static WifiHelper getInstance() {

        try {
            LOCK.lock();

            if (null == mInstance) {
                mInstance = new WifiHelper();
            }
        } finally {
            LOCK.unlock();
        }
        return mInstance;
    }

    private WifiManager mWifiManager;
    private NetworkInfo mNetworkInfo;

    private WifiHelper() {
        mWifiManager = (WifiManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 关闭手机Wi-Fi
     */
    public void closeWifi() {
        mWifiManager.setWifiEnabled(false);
    }

    /**
     * s
     * 打开手机Wi-Fi
     */
    public void openWifi() {
        if (!isWifiEnable()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 检查手机Wi-Fi是否已经打开
     *
     * @return
     */
    private boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }


    /**
     * 检查是否已经存在的Wi-Fi
     *
     * @param paramString
     * @return
     */
    private WifiConfiguration isExists(String paramString) {
        if (mWifiManager == null || mWifiManager.getConfiguredNetworks() == null) return null;
        Iterator<WifiConfiguration> localIterator = mWifiManager.getConfiguredNetworks().iterator();
        WifiConfiguration localWifiConfiguration;
        do {
            if (!localIterator.hasNext())
                return null;
            localWifiConfiguration = localIterator.next();
            LogcatUtils.d(TAG, "isExists: " + localWifiConfiguration.SSID);
        }
        while (!localWifiConfiguration.SSID.equals("\"" + paramString + "\""));
        return localWifiConfiguration;
    }

    /**
     * 检查Wi-Fi网络是否可用
     *
     * @return
     */
    public boolean isWifiConnect() {
        mNetworkInfo = ((ConnectivityManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE))
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean f = mNetworkInfo.isConnected();
        if (f) return f;
        f = mNetworkInfo.isAvailable();
        return f;
    }
}
