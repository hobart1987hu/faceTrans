package org.hobart.facetrans.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.event.ScanWifiEvent;
import org.hobart.facetrans.util.LogcatUtils;

import java.util.List;

/**
 * 扫描附近Wi-Fi线程
 * Created by huzeyin on 2017/12/19.
 */

public class ScanNearbyWifiThread implements Runnable {

    private WifiManager wifiManager;

    @Override
    public void run() {
        wifiManager = (WifiManager) FaceTransApplication.getFaceTransApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        countDownTimer.start();
    }

    private CountDownTimer countDownTimer = new CountDownTimer(10 * 1000, 1 * 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            wifiManager.startScan();

            List<ScanResult> results = wifiManager.getScanResults();
            if (null == results || results.size() <= 0) {
                return;
            }
            for (ScanResult result : results) {

                LogcatUtils.d("ScanNearbyWifiThread  ScanResult ssid:" + result.SSID);

                if (result.SSID.contains(GlobalConfig.AP_SSID_PREFIX)) {
                    //创建的Wi-Fi热点
                    postScanResult(result.SSID, ScanWifiEvent.SCAN_SUCCESS);
                }
            }
        }

        @Override
        public void onFinish() {
            postScanResult("", ScanWifiEvent.SCAN_FAILED);
        }
    };

    private void postScanResult(String ssid, int result) {
        ScanWifiEvent event = new ScanWifiEvent();
        event.status = result;
        event.ssid = ssid;
        EventBus.getDefault().post(event);
    }

    public void stop() {
        if (null != countDownTimer) countDownTimer.cancel();
    }
}
