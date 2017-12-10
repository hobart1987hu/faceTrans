package org.hobart.facetrans.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
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

    private static final String LOG_PREFIX = "WifiHelper-->";

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
    private ConnectivityManager mConnectivityManager;
    private NetworkInfo mNetworkInfo;

    private WifiHelper() {
        mWifiManager = (WifiManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 关闭手机Wi-Fi
     */
    public void closeWifi() {
        if (isWifiEnable())
            mWifiManager.setWifiEnabled(false);
    }

    /**
     * 打开手机Wi-Fi
     */
    public void openWifi() {
        if (!isWifiEnable())
            mWifiManager.setWifiEnabled(true);
    }

    /**
     * 检查手机Wi-Fi是否已经打开
     *
     * @return
     */
    public boolean isWifiEnable() {
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
            LogcatUtils.d(LOG_PREFIX + " wifi isExists: " + localWifiConfiguration.SSID);
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

    public String getSSID() {
        if (mWifiManager.getConnectionInfo() == null)
            return "NULL";
        String ssid = mWifiManager.getConnectionInfo().getSSID();
        if (ssid == null || ssid.trim().equals("")) {
            return "";
        }
        ssid = ssid.replaceAll("\"", "");
        return ssid;
    }

    public String getLocalIPAddress() {
        if (mWifiManager.getConnectionInfo() == null)
            return "NULL";
        return intToIp(mWifiManager.getConnectionInfo().getIpAddress());
    }

    /**
     * 开启热点之后，获取自身热点的IP地址
     * @return
     */
    public String getHotspotLocalIpAddress(){
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        String ipAddress = "192.168.43.1";
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        int address = dhcpInfo.serverAddress;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
        return ipAddress;
    }
    /**
     * 设备连接Wifi之后， 设备获取Wifi热点的IP地址
     * @return
     */
    public String getIpAddressFromHotspot(){
        // WifiAP ip address is hardcoded in Android.
        /* IP/netmask: 192.168.43.1/255.255.255.0 */
        String ipAddress = "192.168.43.1";
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        int address = dhcpInfo.gateway;
        ipAddress = ((address & 0xFF)
                + "." + ((address >> 8) & 0xFF)
                + "." + ((address >> 16) & 0xFF)
                + "." + ((address >> 24) & 0xFF));
        return ipAddress;
    }

    private String intToIp(int paramIntip) {
        return (paramIntip & 0xFF) + "." + ((paramIntip >> 8) & 0xFF) + "."
                + ((paramIntip >> 16) & 0xFF) + "." + ((paramIntip >> 24) & 0xFF);
    }
}
