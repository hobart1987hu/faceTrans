package org.hobart.facetrans.ui.activity;

import android.animation.ValueAnimator;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.FTFilesChangedEvent;
import org.hobart.facetrans.event.ScanWifiEvent;
import org.hobart.facetrans.event.SocketConnectEvent;
import org.hobart.facetrans.event.SocketTransferEvent;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.ScanApUser;
import org.hobart.facetrans.socket.transfer.TransferDataQueue;
import org.hobart.facetrans.socket.transfer.TransferProtocol;
import org.hobart.facetrans.ui.activity.base.BaseTitleBarActivity;
import org.hobart.facetrans.ui.adapter.ScanApWifiGalleryAdapter;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.ui.widget.RadarView;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.ScanNearbyWifiThread;
import org.hobart.facetrans.wifi.WifiHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 扫描接收者
 * Created by huzeyin on 2017/11/28.
 */

public class ScanReceiverActivity extends BaseTitleBarActivity {

    private static final String LOG_PREFIX = "ScanReceiverActivity-->";

    private static final int[] SCAN_USER_ICONS = {R.mipmap.icon_scan_user_1, R.mipmap.icon_scan_user_2, R.mipmap.icon_scan_user_3, R.mipmap.icon_scan_user_4, R.mipmap.icon_scan_user_5, R.mipmap.icon_scan_user_6};

    private RadarView radarView;
    private TextView tv_info;

    private boolean isOpenWifi = false;

    private ScanNearbyWifiThread mScanNearbyWifiThread;
    private RecyclerView mRecycleView;
    private ScanApWifiGalleryAdapter mGalleryViewPagerAdapter;

    private RelativeLayout rl_connect_ap;

    private TextView tv_connectDeviceInfo;
    private ImageView rocket;

    private int screenHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_receiver);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();

        radarView = (RadarView) findViewById(R.id.radarView);
        tv_info = (TextView) findViewById(R.id.tv_info);

        rl_connect_ap = (RelativeLayout) findViewById(R.id.rl_connect_ap);
        tv_connectDeviceInfo = (TextView) findViewById(R.id.tv_connectDeviceInfo);
        rocket = (ImageView) findViewById(R.id.rocket);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, screenHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rl_connect_ap.setTranslationY(value);
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(0);
        valueAnimator.start();

        mRecycleView = (RecyclerView) findViewById(R.id.recycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecycleView.setLayoutManager(linearLayoutManager);

        mGalleryViewPagerAdapter = new ScanApWifiGalleryAdapter(this, mScanApUsers, new OnRecyclerViewClickListener.SimpleOnRecyclerViewClickListener() {
            @Override
            public void onItemClick(View container, View view, int position) {
                synchronized (mScanApUsers) {
                    mSelectedSSID = mScanApUsers.get(position).getUserSSID();
                    showConnectingView(mSelectedSSID);
                    joinAp(mSelectedSSID);
                }
            }
        });
        mRecycleView.setAdapter(mGalleryViewPagerAdapter);

        startScanNearbyWifiThread();
    }

    private void showConnectingView(String deviceId) {
        rl_connect_ap.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(screenHeight, 0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rl_connect_ap.setTranslationY(value);
                if (value == 0) {
                    radarView.stopRotate();
                }
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.start();
        tv_connectDeviceInfo.setText(Html.fromHtml("正在连接设备\n<font color=#ffff38>" + deviceId + "</font>"));
    }

    private void showResetView() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, screenHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rl_connect_ap.setTranslationY(value);
                if (value == screenHeight) {
                    radarView.startRotate();
                    rl_connect_ap.setVisibility(View.GONE);
                    tv_info.setText("正在寻找周围的接收者");
                }
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    private void startScanNearbyWifiThread() {
        mScanNearbyWifiThread = new ScanNearbyWifiThread();
        new Thread(mScanNearbyWifiThread).start();
    }

    private void joinAp(final String ssid) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                isConnectSuccess = false;
                hasConnectedWifi = false;
                mConnectApCountDownTimer.start();
                if (!WifiHelper.getInstance().isWifiEnable()) {
                    isOpenWifi = true;
                    ApWifiHelper.getInstance().closeWifiAp();
                    WifiHelper.getInstance().openWifi();
                } else {
                    connectApWifi(ssid);
                }
                registerWifiBroadcast();
            }
        }).start();
    }

    private CountDownTimer mConnectApCountDownTimer = new CountDownTimer(10 * 1000, 1 * 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            if (isConnectSuccess) return;
            showResetView();
            ToastUtils.showLongToast("连接Wi-Fi热点超时，请重新连接！");
        }
    };

    private void connectApWifi(String ssid) {
        ApWifiHelper.getInstance().closeWifiAp();
        WifiHelper.getInstance().openWifi();
        if (WifiHelper.getInstance().isWifiConnect())
            WifiHelper.getInstance().disableCurrentNetWork();
        ApWifiHelper.getInstance().connectApWifi(ssid, GlobalConfig.AP_PWD);
    }

    private void registerWifiBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        if (null == mWifiBroadcast) {
            mWifiBroadcast = new WifiBroadcast();
            registerReceiver(mWifiBroadcast, filter);
        }
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
                    if (isOpenWifi) connectApWifi(mSelectedSSID);
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
                            if (ssid != null && ssid.equals(mSelectedSSID)) {
                                if (!hasConnectedWifi) {
                                    openClientServiceAndConnectServer();
                                    hasConnectedWifi = true;
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    private void openClientServiceAndConnectServer() {
        String localIp = WifiHelper.getInstance().getLocalIPAddress();
        int lastPointIndex = localIp.lastIndexOf(".");
        String host = localIp.substring(0, lastPointIndex) + ".1";
        LogcatUtils.d(LOG_PREFIX + "openClientServiceAndConnectServer: host" + host + ":::本地地址:::" + localIp);
        IntentUtils.startSocketSenderService(getApplicationContext(), host);
    }

    private List<ScanApUser> mScanApUsers = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanWifiCallback(ScanWifiEvent event) {

        if (null == event) return;

        if (event.status == ScanWifiEvent.SCAN_SUCCESS) {
            showScanApWifi(event.ssid);
        } else {
            if (mScanApUsers.size() <= 0)
                ToastUtils.showLongToast("扫描Wi-Fi失败，正在进行重试...");
            startScanNearbyWifiThread();
        }
    }

    private String mSelectedSSID;

    private void showScanApWifi(final String ssid) {
        synchronized (mScanApUsers) {
            for (ScanApUser scanApUser : mScanApUsers) {
                if (scanApUser.getUserSSID().equals(ssid)) {
                    return;
                }
            }
            final int size = mScanApUsers.size();
            ScanApUser scanApUser = new ScanApUser();
            scanApUser.setUserSSID(ssid);
            int userIcon = -1;
            if (size >= SCAN_USER_ICONS.length) {
                userIcon = SCAN_USER_ICONS[0];
            } else {
                userIcon = SCAN_USER_ICONS[size];
            }
            scanApUser.setUserIcon(userIcon);
            mScanApUsers.add(scanApUser);
            mGalleryViewPagerAdapter.notifyDataSetChanged();
        }
    }

    private boolean isConnectSuccess = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketConnectEvent(SocketConnectEvent event) {
        if (event == null) {
            return;
        }
        switch (event.status) {
            case SocketConnectEvent.CONNECTED_SUCCESS:
                isConnectSuccess = true;
                mConnectApCountDownTimer.cancel();
                tv_connectDeviceInfo.setText("正在与Wi-Fi热点进行信号确认");
                TransferDataQueue.getInstance().sendAckSignal(mSelectedSSID);
                break;
            case SocketConnectEvent.CONNECTED_FAILED:
                isConnectSuccess = false;
                mSelectedSSID = null;
                mConnectApCountDownTimer.cancel();
                ToastUtils.showLongToast("连接失败！");
                IntentUtils.stopSocketSenderService(getApplicationContext());
                showResetView();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SocketTransferEvent(SocketTransferEvent event) {

        if (null == event) return;

        if (event.connectStatus == SocketTransferEvent.SOCKET_CONNECT_FAILURE) {
            ToastUtils.showLongToast("网络异常，请重新连接!");
            isConnectSuccess = false;
            mSelectedSSID = null;
            showResetView();
            return;
        }
        if (event.type == TransferProtocol.TYPE_CONFIRM_ACK) {
            TransferProtocol transferProtocol = new TransferProtocol();
            transferProtocol.ssid = event.ssid;
            transferProtocol.ssm = event.ssm;
            transferProtocol.type = TransferProtocol.TYPE_CONFIRM_ACK;
            TransferDataQueue.getInstance().sendAckConfirmSignal(transferProtocol);

            tv_connectDeviceInfo.setText("Wi-Fi热点信号确认成功，开始进行数据传递！");

            starRotationAnimation();
        } else if (event.type == TransferProtocol.TYPE_MISS_MATCH) {
            ToastUtils.showLongToast("没有连接到\n" + mSelectedSSID + "网络，请重试！");
            isConnectSuccess = false;
            mSelectedSSID = null;
            showResetView();
        } else if (event.type == TransferProtocol.TYPE_DISCONNECT) {
            isConnectSuccess = false;
            mSelectedSSID = null;
            clearAll();
        }
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
        EventBus.getDefault().unregister(this);
        if (mWifiBroadcast != null) unregisterReceiver(mWifiBroadcast);
        if (null != mConnectApCountDownTimer) mConnectApCountDownTimer.cancel();
        if (null != mScanNearbyWifiThread) mScanNearbyWifiThread.stop();
        super.onDestroy();
    }

    @Override
    protected boolean handleViewOnClick() {
        clearAll();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearAll();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clearAll() {
        isConnectSuccess = false;
        mSelectedSSID = null;
        hasConnectedWifi = false;
        EventBus.getDefault().unregister(this);
        FTFileManager.getInstance().clear();
        EventBus.getDefault().post(new FTFilesChangedEvent());
        IntentUtils.stopSocketSenderService(getApplicationContext());
        ApWifiHelper.getInstance().closeWifiAp();
        WifiHelper.getInstance().openWifi();
        if (WifiHelper.getInstance().isWifiConnect())
            WifiHelper.getInstance().disableCurrentNetWork();
        finish();
    }

    private void starRotationAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 360f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rocket.setRotation(value);
                if (value >= 360 && isConnectSuccess) {
                    startAccelerateRocketAnimation();
                }
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }

    private void startAccelerateRocketAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, -screenHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                rocket.setTranslationY(value);
                if (value == (-screenHeight) && isConnectSuccess) {
                    IntentUtils.intentToSendFileActivity(ScanReceiverActivity.this);
                    finish();
                }
            }
        });
        valueAnimator.setInterpolator(new AccelerateInterpolator(3.5f));
        valueAnimator.setDuration(1000);
        valueAnimator.start();
    }
}
