package org.hobart.facetrans.socket;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketClientManager {
//    private static final String TAG = ClientService.class.getSimpleName();
//    private Context mContext;
//    ExecutorService mExecutor = Executors.newCachedThreadPool();
//    //Socket接收线程
//    SocketReceiverService receiverService;
//    //Socket 发送线程
//    SocketSendService sendService;
//    Socket mSocket;
//    HeartBeatRunable heartBeatRunable = null;
//    Future heartBeatFuture = null;
//    SocketCommendManager commendManager;
//
//    public SocketClientManager(Context mContext, Socket socket) {
//        this.mContext = mContext;
//        mSocket = socket;
//        launchSocketTask(mSocket);
//        commendManager = SocketCommendManager.getInstance();
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
//    public void startMonitor() {
//        Log.d(TAG, "startMonitor: 开始心跳");
//        heartBeatRunable = new HeartBeatRunable();
//        heartBeatFuture = mExecutor.submit(heartBeatRunable);
//    }
//
//    public void stopMonitor() {
//        if (heartBeatFuture != null && heartBeatRunable != null) {
//            Log.d(TAG, "stopMonitor: 关闭心跳");
//            heartBeatRunable.setMonitor(false);
//            heartBeatFuture.cancel(true);
//        }
//    }
//
//
//    class HeartBeatRunable implements Runnable {
//        volatile boolean monitor = true;
//
//        @Override
//        public void run() {
//            while (monitor) {
//                try {
//                    commendManager.sendHeartMsg();
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void setMonitor(boolean monitor) {
//            this.monitor = monitor;
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
//
//        if (sendService != null) {
//            sendService.setMonitor(false);
//            sendService.closeStreame();
//        }
//
//        if (mExecutor != null) {
//            mExecutor.shutdownNow();
//        }
//    }
//
//    //释放资源
//    public void release() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                stopMonitor();
//                releaseService();
//                releaseSocket();
//            }
//        }).start();
//    }
}
