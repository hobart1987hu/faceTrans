package org.hobart.facetrans.socket.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.event.SocketConnectEvent;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.transfer.TransferDataQueue;
import org.hobart.facetrans.socket.transfer.TransferSender;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket 发送端服务
 * Created by huzeyin on 2017/11/7.
 */

public class SocketSenderService extends Service {

    private static final String LOGINFO_PREFIX = "SocketClientService->";
    private String mHost;
    private Socket mSocket = null;
    private int retryCount = 3;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketDisconnect(SocketConnectEvent event) {
        if (null == event) return;
        if (event.status == SocketConnectEvent.DIS_CONNECTED) {
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogcatUtils.d(LOGINFO_PREFIX + "onStartCommand: ");

        if (null == intent) return START_STICKY;

        final String action = intent.getAction();

        LogcatUtils.d(LOGINFO_PREFIX + "action -> " + action);

        if (!TextUtils.isEmpty(action)) {
            if (action.equals(SocketConstants.ACTION_CREATE_CLIENT_SOCKET)) {
                mHost = intent.getStringExtra("host");
                releaseSocket();
                newSocket();
            } else if (action.equals(SocketConstants.ACTION_STOP_CLIENT_SOCKET)) {

                if (null != mTransferSender) {
                    mTransferSender.stopSenderThread();
                }

                if (null != mSocket && mSocket.isConnected()) {
                    TransferDataQueue.getInstance().sendDisconnect();
                } else {
                    stopSelf();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogcatUtils.d(LOGINFO_PREFIX + "onDestroy: ");
        EventBus.getDefault().unregister(this);
        releaseSocket();
    }

    void releaseSocket() {

        if (null != mTransferSender) mTransferSender.release();

        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void newSocket() {
        retryCount = 3;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (retryCount > 0) {
                    try {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        mSocket = new Socket();
                        mSocket.setKeepAlive(true);
                        mSocket.setTcpNoDelay(true);
                        mSocket.setSoLinger(true, 0);
                        mSocket.setSendBufferSize(SocketConstants.TCP_BUFFER_SIZE);
                        mSocket.setReceiveBufferSize(SocketConstants.TCP_BUFFER_SIZE);
                        mSocket.connect(new InetSocketAddress(mHost, SocketConstants.SERVER_PORT), SocketConstants.SOCKET_CONNECTED_TIME_OUT);
                        initTransferSender();
                        postSocketConnectedStatus(SocketConnectEvent.CONNECTED_SUCCESS);
                        LogcatUtils.d(LOGINFO_PREFIX + "mSocket 发送端 创建成功");
                        retryCount = 0;
                    } catch (Exception e) {
                        retryCount--;
                        if (retryCount <= 0) {
                            LogcatUtils.d(LOGINFO_PREFIX + " mSocket 发送端 创建失败 ");
                            postSocketConnectedStatus(SocketConnectEvent.CONNECTED_FAILED);
                        }
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private TransferSender mTransferSender;

    private void initTransferSender() {
        mTransferSender = new TransferSender(mSocket);
    }

    private void postSocketConnectedStatus(int status) {
        SocketConnectEvent statusBean = new SocketConnectEvent();
        statusBean.status = status;
        EventBus.getDefault().post(statusBean);
    }
}
