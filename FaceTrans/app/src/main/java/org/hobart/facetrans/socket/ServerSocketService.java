package org.hobart.facetrans.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.SocketStatusEvent;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * server端服务开启
 * Created by huzeyin on 2017/11/7.
 */

public class ServerSocketService extends Service {

    private static final String LOG_PREFIX = "ServerSocketService->";
    private ServerSocket mSocketService;
    private volatile boolean monitor = true;
    private Socket mSocket = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogcatUtils.d(LOG_PREFIX + "----onBind----");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogcatUtils.d(LOG_PREFIX + "----onCreate----");
    }

    private void createServerSocket() {
        releaseSocket();
        try {
            LogcatUtils.d(LOG_PREFIX + "--createServerSocket--");
            mSocketService = new ServerSocket();
            mSocketService.setReceiveBufferSize(SocketConstants.TCP_BUFFER_SIZE);
            mSocketService.bind(new InetSocketAddress(SocketConstants.SERVER_PORT));
            monitor = true;
            //一直等待客户端
            acceptClient();
        } catch (IOException e) {
            postSocketConnectedStatus(SocketStatusEvent.CONNECTED_FAILED);
            e.printStackTrace();
        }
    }

    private void acceptClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogcatUtils.d(LOG_PREFIX + "--acceptClient run--");
                while (monitor) {
                    try {
                        mSocket = mSocketService.accept();
                        //一直阻塞在这里
                        monitor = false;
                        postSocketConnectedStatus(SocketStatusEvent.CONNECTED_SUCCESS);
                        LogcatUtils.d(LOG_PREFIX + "--客户端接收成功--");
                    } catch (IOException e) {
                        postSocketConnectedStatus(SocketStatusEvent.CONNECTED_FAILED);
                        monitor = false;
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogcatUtils.d(LOG_PREFIX + "----onStartCommand----");
        if (null == intent) return START_STICKY;
        final String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(SocketConstants.ACTION_CREATE_SERVER_SOCKET)) {
                createServerSocket();
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
        SocketStatusEvent statusBean = new SocketStatusEvent();
        statusBean.status = status;
        EventBus.getDefault().post(statusBean);
    }
}
