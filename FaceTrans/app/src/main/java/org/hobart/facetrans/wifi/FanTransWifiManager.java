package org.hobart.facetrans.wifi;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class FanTransWifiManager {

    private ApWifiHelper mApWifiHelper = ApWifiHelper.getInstance();

    private WifiHelper mWifiHelper = WifiHelper.getInstance();

    public void createWifiAP(final String ssid, final String pwd) {
        mWifiHelper.closeWifi();
        mApWifiHelper.createWifiAP(ssid, pwd);
    }

    public boolean connectApWifi(String ssid, String pwd) {
        return false;
//        mApWifiUtil.closeWifiAp();
//        wifiUtil.openWifi(context);
//        boolean connected = false;
//        if (wifiUtil.isWifiEnable()) {
//            mApWifiUtil.disableCurrentNetWork();
//            connected = mApWifiUtil.ConnectApWifi(ssid, pwd);
//        }
//        return connected;
    }

    public void closeWifiAp() {
        mApWifiHelper.closeWifiAp();
        mWifiHelper.openWifi();
    }
}
