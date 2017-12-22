package org.hobart.facetrans.socket.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.SocketConnectEvent;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.transfer.TransferReceiver;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * server  接收端服务
 * Created by huzeyin on 2017/11/7.
 */

public class ServerReceiverService extends Service {

    private static final String LOG_PREFIX = "ServerSocketService->";
    private ServerSocket mSocketService;
    private volatile boolean monitor = true;
    private Socket mSocket = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogcatUtils.d(LOG_PREFIX + "----onCreate----");
    }

    private void createReceiverSocket() {
        releaseSocket();
        try {
            LogcatUtils.d(LOG_PREFIX + "--createReceiverSocket--");
            mSocketService = new ServerSocket();
            mSocketService.setReceiveBufferSize(SocketConstants.TCP_BUFFER_SIZE);
            mSocketService.bind(new InetSocketAddress(SocketConstants.SERVER_PORT));
            monitor = true;
            acceptSenderClient();
        } catch (IOException e) {
            postSocketConnectedStatus(SocketConnectEvent.CONNECTED_FAILED);
            e.printStackTrace();
        }
    }

    private void acceptSenderClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogcatUtils.d(LOG_PREFIX + "--acceptSenderClient run--");
                while (monitor) {
                    try {
                        mSocket = mSocketService.accept();
                        monitor = false;
                        initTransferReceiver();
                        postSocketConnectedStatus(SocketConnectEvent.CONNECTED_SUCCESS);
                        LogcatUtils.d(LOG_PREFIX + "--发送端连接成功--");
                    } catch (IOException e) {
                        postSocketConnectedStatus(SocketConnectEvent.CONNECTED_FAILED);
                        monitor = false;
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private TransferReceiver mTransferReceiver;

    private void initTransferReceiver() {
        mTransferReceiver = new TransferReceiver(mSocket);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogcatUtils.d(LOG_PREFIX + "----onStartCommand----");
        if (null == intent) return START_STICKY;
        final String action = intent.getAction();

        LogcatUtils.d(LOG_PREFIX + "----action :" + action);

        if (!TextUtils.isEmpty(action)) {
            if (action.equals(SocketConstants.ACTION_CREATE_SERVER_SOCKET)) {
                createReceiverSocket();
            } else if (action.equals(SocketConstants.ACTION_STOP_SERVER_SOCKET)) {
                releaseSocket();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogcatUtils.d(LOG_PREFIX + "----onDestroy----");
        releaseSocket();
    }

    void releaseSocket() {

        monitor = false;

        if (null != mTransferReceiver)
            mTransferReceiver.release();

        if (mSocketService != null && !mSocketService.isClosed()) {
            try {
                mSocketService.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void postSocketConnectedStatus(int status) {
        SocketConnectEvent statusBean = new SocketConnectEvent();
        statusBean.status = status;
        EventBus.getDefault().post(statusBean);
    }
}
