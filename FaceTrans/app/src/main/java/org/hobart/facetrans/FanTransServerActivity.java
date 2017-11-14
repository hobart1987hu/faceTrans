package org.hobart.facetrans;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;

import com.zxing.android.qrcode.QRCodeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.event.ApCreateEvent;
import org.hobart.facetrans.event.SocketStatusEvent;
import org.hobart.facetrans.socket.ServerService;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.util.WifiTools;
import org.hobart.facetrans.wifi.CreateWifiAPThread;
import org.hobart.facetrans.wifi.FanTransWifiManager;


/**
 * 创建二维码界面
 * Created by huzeyin on 2017/10/30.
 */

public class FanTransServerActivity extends Activity {

    private static final String TAG = "BuildQRCodeActivity";

    private FanTransWifiManager mFanTransWifiManager;
    private CreateWifiAPThread mCreateWifiAPThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_qr_code);
        init();
        createWifiAp();
    }

    private ViewStub mViewStub;
    private ImageView mQrImg;

    private void init() {
        mViewStub = (ViewStub) findViewById(R.id.mViewStub);
        mFanTransWifiManager = new FanTransWifiManager();
        EventBus.getDefault().register(this);
    }

    private String mSSID;
    private String mPWD;

    /**
     * 创建Wi-Fi热点
     */
    private void createWifiAp() {
        mSSID = AndroidUtils.getDeviceModel();
        mPWD = WifiTools.getRandomPwd();
        mFanTransWifiManager.createWifiAP(mSSID, mPWD);
        mCreateWifiAPThread = new CreateWifiAPThread();
        new Thread(mCreateWifiAPThread).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFanTransWifiManager.closeWifiAp();
        EventBus.getDefault().unregister(this);
        if (null != mCreateWifiAPThread) mCreateWifiAPThread.cancelDownTimer();
        if (mWifiAPConnectedReceiver != null) {
            unregisterReceiver(mWifiAPConnectedReceiver);
        }
    }

    //创建二维码
    private void buildQrCode() {
        View qrView = mViewStub.inflate();
        mQrImg = (ImageView) qrView.findViewById(R.id.qr_code_img);
        String rQContent = "SSID:" + mSSID + ":Pwd:" + mPWD;
        Bitmap bitmap = QRCodeUtil.createQRImage(rQContent);
        mQrImg.setImageBitmap(bitmap);
        startServerService();
        registerApWifiStatus();
    }

    private void startServerService() {
        startService(new Intent(this, ServerService.class));
    }

    private void stopServerService() {
        stopService(new Intent(this, ServerService.class));
    }

    private boolean connectedSuccess = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiAPCreateCallBack(ApCreateEvent event) {
        if (event == null) {
            return;
        }
        switch (event.status) {
            case ApCreateEvent.SUCCESS:
                LogcatUtils.d(TAG, "onSuccess: 热点创建成功");
                if (!connectedSuccess) {
                    buildQrCode();
                }
                connectedSuccess = true;
                break;
            case ApCreateEvent.FAILED:
                LogcatUtils.d(TAG, "onFailed: 热点创建失败");
                ToastUtils.showLongToast("Wi-Fi热点创建失败！");
                finish();
                break;
            default:
                break;
        }
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
                if (state == 11) {
                    ToastUtils.showLongToast("热点已关闭");
                    finish();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketStatusEvent(SocketStatusEvent bean) {
        LogcatUtils.d(TAG, "----onSocketStatusEvent---");
        if (bean == null) {
            return;
        }
        switch (bean.status) {
            case SocketStatusEvent.CONNECTED_SUCCESS:
                LogcatUtils.d(TAG, "----onSocketStatusEvent 创建成功---");
                startActivity(new Intent(this, TransferActivity.class));
                finish();
                break;
            case SocketStatusEvent.CONNECTED_FAILED:
                ToastUtils.showLongToast("创建服务端失败！");
                stopServerService();
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            stopServerService();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
