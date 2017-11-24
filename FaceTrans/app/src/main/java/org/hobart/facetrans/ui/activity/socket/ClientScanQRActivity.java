package org.hobart.facetrans.ui.activity.socket;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zxing.android.camera.CameraManager;
import com.zxing.android.decoding.CameraDecodeProvider;
import com.zxing.android.decoding.CaptureActivityHandlerForFace;
import com.zxing.android.view.ViewfinderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.SocketStatusEvent;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.WifiHelper;

import java.io.IOException;
import java.util.Vector;

import rx.functions.Action1;

/**
 * 客户端扫描二维码界面
 * Created by huzeyin on 2017/11/14.
 */

public class ClientScanQRActivity extends Activity implements SurfaceHolder.Callback, CameraDecodeProvider {

    private static final String LOG_PREFIX = "ClientScanQRActivity-->";

    private CaptureActivityHandlerForFace mHandler;
    private CameraManager mCameraManager;
    private ViewfinderView mViewFindView;
    private SurfaceView mSurfaceView;
    private boolean hasSurface;
    private boolean vibrate;
    private boolean playBeep;
    private MediaPlayer mediaPlayer;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_scan_qr);
        initView();
        EventBus.getDefault().register(this);
        new RxPermissions(this).request(Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            ToastUtils.showLongToast("用户拒绝照相机");
                            finish();
                        }
                    }
                });
    }

    private void initView() {
        mViewFindView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        hasSurface = false;
        mCameraManager = new CameraManager(getApplication());
        mViewFindView.setCameraManager(mCameraManager);
    }

    private void initCamera() {
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setLooping(false);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.stop();
                }
            });
            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(0.10f, 123);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mHandler) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mCameraManager.closeDriver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IntentUtils.stopClientSocketService(this);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mCameraManager.closeDriver();
        if (mWifiBroadcast != null) unregisterReceiver(mWifiBroadcast);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            mCameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (mHandler == null) {
            mHandler = new CaptureActivityHandlerForFace(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(200L);
        }
    }

    @Override
    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public ViewfinderView getViewfinderView() {
        return mViewFindView;
    }

    @Override
    public Activity getView() {
        return this;
    }

    private String mSSID;
    private String mPwd;

    private boolean openWifi = false;

    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        LogcatUtils.d(LOG_PREFIX + "handleDecode 扫描结果为->" + resultString);

        if (resultString.equals("")) {
            ToastUtils.showLongToast("扫描失败！");
        } else {
            String[] apWifiInfo = resultString.split(":");
            int size = apWifiInfo.length;
            if (size == 4) {
                mSSID = apWifiInfo[1];
                mPwd = apWifiInfo[3];
                ToastUtils.showLongToast("扫描成功，正在链接网络");
                if (!WifiHelper.getInstance().isWifiEnable()) {
                    openWifi = true;
                    //关闭热点Wi-Fi
                    ApWifiHelper.getInstance().closeWifiAp();
                    //打开Wi-Fi
                    WifiHelper.getInstance().openWifi();
                } else {
                    //连接热点
                    connectApWifi();
                }
                mSurfaceView.setVisibility(View.GONE);
                mViewFindView.setVisibility(View.GONE);
                registerWifiBroadcast();
            } else {
                ToastUtils.showLongToast("获取二维码失败！");
            }
        }
    }

    private void connectApWifi() {
        ApWifiHelper.getInstance().closeWifiAp();
        WifiHelper.getInstance().openWifi();
        if (WifiHelper.getInstance().isWifiConnect())
            WifiHelper.getInstance().disableCurrentNetWork();
        ApWifiHelper.getInstance().connectApWifi(mSSID, mPwd);
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
                            if (ssid != null && ssid.equals(mSSID)) {
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
}
