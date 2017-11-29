package org.hobart.facetrans.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.ApCreateEvent;
import org.hobart.facetrans.event.SocketStatusEvent;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.widget.RippleImageView;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.CreateWifiAPThread;
import org.hobart.facetrans.wifi.WifiHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 扫描发送者
 * Created by huzeyin on 2017/11/28.
 */

public class ScanSenderActivity extends BaseActivity {

    private static final String LOG_PREFIX = "ScanSenderActivity->";

    @Bind(R.id.rippleImageView)
    RippleImageView rippleImageView;

    private CreateWifiAPThread mCreateWifiAPThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_sender);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        createAp();
    }

    private void createAp() {
        WifiHelper.getInstance().closeWifi();
        ApWifiHelper.getInstance().createWifiAP(GlobalConfig.AP_SSID, GlobalConfig.AP_PWD);
        mCreateWifiAPThread = new CreateWifiAPThread();
        new Thread(mCreateWifiAPThread).start();
    }

    private ApWifiConnectedReceiver mWifiAPConnectedReceiver;

    private void registerApWifiStatus() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mWifiAPConnectedReceiver = new ApWifiConnectedReceiver();
        registerReceiver(mWifiAPConnectedReceiver, filter);
    }

    class ApWifiConnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                //便携式热点的状态为：10---正在关闭；11---已关闭；12---正在开启；13---已开启
                int state = intent.getIntExtra("wifi_state", 0);
//                if (state == 10) {
//                    ToastUtils.showLongToast("热点关闭");
//                    finish();
//                }
            }
        }
    }

    private boolean connectedSuccess = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiAPCreateCallBack(ApCreateEvent event) {
        if (event == null) {
            return;
        }
        switch (event.status) {
            case ApCreateEvent.SUCCESS:
                ToastUtils.showLongToast("Wi-Fi热点创建成功");
                LogcatUtils.d(LOG_PREFIX + "onWifiAPCreateCallBack onSuccess: 热点创建成功");
                if (!connectedSuccess) {
                    IntentUtils.startServerSocketService(this);
                    registerApWifiStatus();
                }
                connectedSuccess = true;
                break;
            case ApCreateEvent.FAILED:
                LogcatUtils.d(LOG_PREFIX + "onWifiAPCreateCallBack onFailed: 热点创建失败");
                ToastUtils.showLongToast("Wi-Fi热点创建失败！");
                finish();
                break;
            default:
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketStatusEvent(SocketStatusEvent bean) {
        if (bean == null) {
            return;
        }
        switch (bean.status) {
            case SocketStatusEvent.CONNECTED_SUCCESS:
//                ToastUtils.showLongToast("创建发送端成功！");
                IntentUtils.intentToReceiveFileActivity(this);
                finish();
                break;
            case SocketStatusEvent.CONNECTED_FAILED:
//                ToastUtils.showLongToast("创建发送端失败！");
                IntentUtils.stopServerReceiverService(this);
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rippleImageView.startWaveAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rippleImageView.stopWaveAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != mCreateWifiAPThread) mCreateWifiAPThread.cancelDownTimer();
        if (null != mWifiAPConnectedReceiver)
            unregisterReceiver(mWifiAPConnectedReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IntentUtils.stopServerReceiverService(this);
            ApWifiHelper.getInstance().closeWifiAp();
            WifiHelper.getInstance().openWifi();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
