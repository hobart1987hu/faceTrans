package org.hobart.facetrans.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.util.LogcatUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 热点Wi-Fi 帮助类
 * Created by huzeyin on 2017/11/7.
 */

public class ApWifiHelper {

    private static ApWifiHelper mInstance = null;

    private static final ReentrantLock LOCK = new ReentrantLock();


    public static ApWifiHelper getInstance() {

        try {
            LOCK.lock();

            if (null == mInstance) {
                mInstance = new ApWifiHelper();
            }
        } finally {
            LOCK.unlock();
        }
        return mInstance;
    }

    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;

    private ApWifiHelper() {
        mWifiManager = (WifiManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 检查Wi-Fi热点是否已经打开
     *
     * @return
     */
    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建Wi-Fi热点
     *
     * @param ssid
     * @param password
     */
    public void createWifiAP(String ssid, String password) {

        Method method1 = null;
        try {
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = new WifiConfiguration();
            netConfig.SSID = ssid;
            netConfig.preSharedKey = password;
            netConfig.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement
                    .set(WifiConfiguration.KeyMgmt.WPA_PSK);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            netConfig.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.CCMP);
            netConfig.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.TKIP);
            method1.invoke(mWifiManager, netConfig, true);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭Wi-Fi 热点
     */
    public void closeWifiAp() {
        if (getWifiAPState() != WIFI_AP_STATE_DISABLED) {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取Wi-Fi热点状态
     *
     * @return
     */
    private int getWifiAPState() {
        int state = -1;
        try {
            Method method2 = mWifiManager.getClass().getMethod("getWifiApState");
            state = (Integer) method2.invoke(mWifiManager);
        } catch (Exception e) {
            LogcatUtils.e("ApWifiHelper->getWifiAPState exception ->" + e.getMessage());
        }
        return state;
    }

    //连接Wpa wifi
    public boolean connectApWifi(String networkSSID, String password) {
        try {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";
            conf.preSharedKey = "\"" + password + "\"";
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            int netWorkId = mWifiManager.addNetwork(conf);
            List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    mWifiManager.disconnect();
                    mWifiManager.enableNetwork(i.networkId, true);
                    mWifiManager.reconnect();
                    break;
                }
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void disableCurrentNetWork() {
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (mNetworkInfo != null) {
            if (mNetworkInfo.isConnected()) {//判断wifi 是否连接
                int networkId = wifiInfo.getNetworkId();
                mWifiManager.disableNetwork(networkId);
                mWifiManager.saveConfiguration();
            }
        }
    }

    private static int WIFI_AP_STATE_DISABLING = 10;
    private static int WIFI_AP_STATE_DISABLED = 11;
    private static int WIFI_AP_STATE_ENABLING = 12;
    private static int WIFI_AP_STATE_ENABLED = 13;
    private static int WIFI_AP_STATE_FAILED = 14;
}
