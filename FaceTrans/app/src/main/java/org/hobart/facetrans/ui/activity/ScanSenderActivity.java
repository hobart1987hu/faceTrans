package org.hobart.facetrans.ui.activity;

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
import org.hobart.facetrans.ui.activity.base.BaseTitleBarActivity;
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

public class ScanSenderActivity extends BaseTitleBarActivity {

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
        // TODO: 如果有其他的热点，我们自己在创建热点是否有影响?
        WifiHelper.getInstance().closeWifi();
        if (null == mCreateWifiAPThread)
            mCreateWifiAPThread = new CreateWifiAPThread();
        new Thread(mCreateWifiAPThread).start();
        ApWifiHelper.getInstance().createWifiAP(GlobalConfig.AP_SSID, GlobalConfig.AP_PWD);
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
                if (!connectedSuccess)
                    IntentUtils.startServerSocketService(this);
                connectedSuccess = true;
                break;
            case ApCreateEvent.FAILED:
                LogcatUtils.d(LOG_PREFIX + "onWifiAPCreateCallBack onFailed: 热点创建失败");
                ToastUtils.showLongToast("Wi-Fi热点创建失败！");
                finish();
                break;
            case ApCreateEvent.TRY_AGAIN:
                createAp();
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
                IntentUtils.intentToReceiveFileActivity(this);
                finish();
                break;
            case SocketStatusEvent.CONNECTED_FAILED:
                ToastUtils.showLongToast("网络创建失败！");
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
        EventBus.getDefault().unregister(this);
        if (null != mCreateWifiAPThread) mCreateWifiAPThread.cancelDownTimer();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //取消注册
            EventBus.getDefault().unregister(this);
            //关闭service
            IntentUtils.stopServerReceiverService(this);
            //关闭热点
            ApWifiHelper.getInstance().closeWifiAp();
            //断开与当前的热点连接
            ApWifiHelper.getInstance().disableCurrentNetWork();
            //重新打开Wi-Fi
            WifiHelper.getInstance().openWifi();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
