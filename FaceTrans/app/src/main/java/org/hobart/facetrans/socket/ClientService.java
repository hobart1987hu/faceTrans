package org.hobart.facetrans.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.SocketStatusEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class ClientService extends Service {
    private static final String TAG = ClientService.class.getSimpleName();
    //ip地址
    String host;
    public SocketBinder mBinder = new SocketBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        host = intent.getStringExtra("host");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind: ");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        releaseSocket();
    }


    void releaseSocket() {
        if (mBinder == null) {
            return;
        }
        if (mBinder.socket != null && !mBinder.socket.isClosed()) {
            try {
                mBinder.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public class SocketBinder extends Binder {
        public Socket socket = null;
        int retryCount = 3;

        //创建socket
        public void newSocket() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (retryCount > 0) {
                        try {
                            SafeMethods.sleep(1000);
                            socket = new Socket();
                            socket.setKeepAlive(true);
                            //socket 及时发送不会缓冲
                            socket.setTcpNoDelay(true);
                            //socket立即关闭
                            socket.setSoLinger(true, 0);
                            socket.setSendBufferSize(SocketConfig.TCP_BUFFER_SIZE);
                            socket.setReceiveBufferSize(SocketConfig.TCP_BUFFER_SIZE);
                            socket.connect(new InetSocketAddress(host, SocketConfig.SERVER_PORT), SocketConfig.SOCKET_CONNECTED_TIME_OUT);
                            postSocketConnectedStatus(SocketStatusEvent.CONNECTED_SUCCESS);
                            Log.d(TAG, "newSocket: 客户端socket 创建成功");
                            retryCount = 0;
                        } catch (Exception e) {
                            retryCount--;
                            if (retryCount <= 0) {
                                Log.d(TAG, "newSocket: 客户端socket 创建失败11111");
                                postSocketConnectedStatus(SocketStatusEvent.CONNECTED_FAILED);
                            }
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        public Socket getSocket() {
            return socket;
        }

        //通知Socket 连接状况
        void postSocketConnectedStatus(int status) {
            SocketStatusEvent statusBean = new SocketStatusEvent();
            statusBean.status = status;
            EventBus.getDefault().post(statusBean);
        }
    }

}
