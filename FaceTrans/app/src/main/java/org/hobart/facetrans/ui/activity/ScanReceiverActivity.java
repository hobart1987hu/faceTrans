package org.hobart.facetrans.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.SocketStatusEvent;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.widget.RadarView;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.WifiHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 扫描接收者
 * Created by huzeyin on 2017/11/28.
 */

public class ScanReceiverActivity extends BaseActivity {

    private static final String LOG_PREFIX = "ScanReceiverActivity-->";

    @Bind(R.id.radarView)
    RadarView radarView;
    @Bind(R.id.tv_info)
    TextView tv_info;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_receiver);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        startCountDownTimer();
        joinAp();
    }

    private boolean openWifi = false;

    /**
     * 加入热点
     */
    private void joinAp() {
        if (!WifiHelper.getInstance().isWifiEnable()) {
            openWifi = true;
            ApWifiHelper.getInstance().closeWifiAp();
            WifiHelper.getInstance().openWifi();
        } else {
            connectApWifi();
        }
        registerWifiBroadcast();
    }

    private void connectApWifi() {
        ApWifiHelper.getInstance().closeWifiAp();
        WifiHelper.getInstance().openWifi();
        if (WifiHelper.getInstance().isWifiConnect())
            WifiHelper.getInstance().disableCurrentNetWork();
        ApWifiHelper.getInstance().connectApWifi(GlobalConfig.AP_SSID, GlobalConfig.AP_PWD);
    }

    private void registerWifiBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (null == mWifiBroadcast) mWifiBroadcast = new WifiBroadcast();
        registerReceiver(mWifiBroadcast, filter);
    }

    private WifiBroadcast mWifiBroadcast;
    private boolean hasConnectedWifi = false;

    private final class WifiBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {//监听wifi 打开关闭
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                    if (openWifi) connectApWifi();
                }
            }
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {//wifi 连接状态
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    switch (state) {
                        case CONNECTED://连接
                            String ssid = WifiHelper.getInstance().getSSID();
                            if (ssid != null && ssid.equals(GlobalConfig.AP_SSID)) {
                                if (!hasConnectedWifi) {
                                    openClientServiceAndConnectServerSocket();
                                    hasConnectedWifi = true;
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    private void openClientServiceAndConnectServerSocket() {
        String localIp = WifiHelper.getInstance().getLocalIPAddress();
        int lastPointIndex = localIp.lastIndexOf(".");
        String host = localIp.substring(0, lastPointIndex) + ".1";
        LogcatUtils.d(LOG_PREFIX + "openClientServiceAndConnectServerSocket: host" + host + ":::本地地址:::" + localIp);
        IntentUtils.startClientSocketService(this, host);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketStatusEvent(SocketStatusEvent bean) {
        if (bean == null) {
            return;
        }
        switch (bean.status) {
            case SocketStatusEvent.CONNECTED_SUCCESS:
                ToastUtils.showLongToast("连接网络成功，可以开始传递数据了！");
                IntentUtils.intentToSendFileActivity(this);
                finish();
                break;
            case SocketStatusEvent.CONNECTED_FAILED:
                IntentUtils.stopClientSocketService(this);
                ToastUtils.showLongToast("连接网络失败！");
                finish();
                break;
            default:
                break;
        }
    }


    private void startCountDownTimer() {
        if (null != mCountDownTimer) mCountDownTimer.cancel();
        mCountDownTimer = new CountDownTimer(10 * 1000, 1 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                ToastUtils.showLongToast("超时啦！");
                finish();
            }
        };
        mCountDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        radarView.startRotate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        radarView.stopRotate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mWifiBroadcast != null) unregisterReceiver(mWifiBroadcast);
        if (null != mCountDownTimer) mCountDownTimer.cancel();
    }

}
