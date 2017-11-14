package org.hobart.facetrans.socket;

import android.content.Context;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketServerManager {
//    private static final String TAG = SocketService.class.getSimpleName();
//    private ExecutorService mExecutor;
//    private Context mContext;
//    Socket mSocket;
//    SocketReceiverService receiverService;
//    SocketSendService sendService;
//
//    public SocketService(Context context, Socket socket) {
//        mContext = context;
//        mExecutor = Executors.newCachedThreadPool();
//        mSocket = socket;
//        launchSocketTask(mSocket);
//
//    }
//
//
//    //启动接收/发送数据线程
//    void launchSocketTask(Socket socket) {
//        if (socket != null && socket.isConnected()) {
//            receiverService = new SocketReceiverService(socket, mContext);
//            sendService = new SocketSendService(socket, mContext);
//            mExecutor.execute(receiverService);
//            mExecutor.execute(sendService);
//        }
//    }
//
//
//    //关闭Socket
//    void releaseSocket() {
//        if (mSocket != null && !mSocket.isClosed()) {
//            try {
//                mSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //关闭线程池
//    void releaseService() {
//        if (receiverService != null) {
//            receiverService.setMonitor(false);
//            receiverService.closeStreame();
//        }
//        if (sendService != null) {
//            sendService.setMonitor(false);
//            sendService.closeStreame();
//        }
//        if (mExecutor != null) {
//            if (!mExecutor.isShutdown()) {
//                mExecutor.shutdownNow();
//            }
//        }
//    }
//
//
//    //释放资源
//    public void release() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                releaseService();
//                releaseSocket();
//            }
//        }).start();
//    }
}
