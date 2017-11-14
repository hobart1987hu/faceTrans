package org.hobart.facetrans.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

public class ServerService extends Service {

    private static final String TAG = ServerService.class.getSimpleName();

    private ServerSocket mSocketService;
    private volatile boolean monitor = true;
    private Socket mSocket = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogcatUtils.d(TAG, "----onBind----");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createServerSocket();
    }

    private void createServerSocket() {
        releaseSocket();
        try {
            LogcatUtils.d(TAG, "--createServerSocket--");
            mSocketService = new ServerSocket();
            mSocketService.setReceiveBufferSize(SocketConfig.TCP_BUFFER_SIZE);
            mSocketService.bind(new InetSocketAddress(SocketConfig.SERVER_PORT));
            monitor = true;
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
                LogcatUtils.d(TAG, "--acceptClient run--");
                while (monitor) {
                    try {
                        mSocket = mSocketService.accept();
                        monitor = false;
                        postSocketConnectedStatus(SocketStatusEvent.CONNECTED_SUCCESS);
                        LogcatUtils.d(TAG, "--客户端接收成功--");
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
        LogcatUtils.d(TAG, "----onStartCommand----");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogcatUtils.d(TAG, "----onDestroy----");
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
