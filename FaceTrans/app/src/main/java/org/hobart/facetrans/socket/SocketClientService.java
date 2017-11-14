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
import java.net.Socket;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketClientService extends Service {

    private static final String LOGINFO_PREFIX = "SocketClientService->";
    private String host;
    private Socket socket = null;
    private int retryCount = 3;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LogcatUtils.d(LOGINFO_PREFIX + "onStartCommand: ");

        if (null == intent) return START_STICKY;

        final String action = intent.getAction();

        if (!TextUtils.isEmpty(action)) {
            if (action.equals(SocketConstants.ACTION_CREATE_CLIENT_SOCKET)) {
                host = intent.getStringExtra("host");
                releaseSocket();
                newSocket();
            } else if (action.equals(SocketConstants.ACTION_STOP_CLIENT_SOCKET)) {
                releaseSocket();
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogcatUtils.d(LOGINFO_PREFIX + "onDestroy: ");
        releaseSocket();
    }


    void releaseSocket() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void newSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (retryCount > 0) {
                    try {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        socket = new Socket();
                        socket.setKeepAlive(true);
                        //socket 及时发送不会缓冲
                        socket.setTcpNoDelay(true);
                        //socket立即关闭
                        socket.setSoLinger(true, 0);
                        socket.setSendBufferSize(SocketConstants.TCP_BUFFER_SIZE);
                        socket.setReceiveBufferSize(SocketConstants.TCP_BUFFER_SIZE);
                        socket.connect(new InetSocketAddress(host, SocketConstants.SERVER_PORT), SocketConstants.SOCKET_CONNECTED_TIME_OUT);
                        postSocketConnectedStatus(SocketStatusEvent.CONNECTED_SUCCESS);
                        LogcatUtils.d(LOGINFO_PREFIX + "客户端socket 创建成功");
                        retryCount = 0;
                    } catch (Exception e) {
                        retryCount--;
                        if (retryCount <= 0) {
                            LogcatUtils.d(LOGINFO_PREFIX + " 客户端socket 创建失败 ");
                            postSocketConnectedStatus(SocketStatusEvent.CONNECTED_FAILED);
                        }
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void postSocketConnectedStatus(int status) {
        SocketStatusEvent statusBean = new SocketStatusEvent();
        statusBean.status = status;
        EventBus.getDefault().post(statusBean);
    }
}
