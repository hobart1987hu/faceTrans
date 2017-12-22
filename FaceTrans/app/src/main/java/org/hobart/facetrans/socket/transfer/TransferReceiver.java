package org.hobart.facetrans.socket.transfer;

import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.socket.transfer.thread.ReceiveRunnable;
import org.hobart.facetrans.socket.transfer.thread.SendRunnable;
import org.hobart.facetrans.util.IntentUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 面传中接收端
 * Created by huzeyin on 2017/11/24.
 */

public class TransferReceiver {

    private ExecutorService mExecute;

    private Socket mSocket;

    private SendRunnable mSendThread;

    private ReceiveRunnable mReceiveThread;

    private ServerInnerSyncThread mServerInnerSyncThread;

    public TransferReceiver(Socket serverSocket) {
        mExecute = Executors.newCachedThreadPool();
        mSocket = serverSocket;
        startServer();
    }

    private void startServer() {
        if (null != mSocket && mSocket.isConnected()) {
            mReceiveThread = new ReceiveRunnable(mSocket);
            mSendThread = new SendRunnable(mSocket);
            mExecute.execute(mReceiveThread);
            mExecute.execute(mSendThread);
            mServerInnerSyncThread = new ServerInnerSyncThread();
            mServerInnerSyncThread.start();
        }
    }

    public void syncTime() {
        if (null != mServerInnerSyncThread)
            mServerInnerSyncThread.updateSyncTime(System.currentTimeMillis());
    }

    final class ServerInnerSyncThread extends Thread {
        volatile long lastSyncTime;
        private Timer mTimer;

        public ServerInnerSyncThread() {
            lastSyncTime = System.currentTimeMillis();
        }
        @Override
        public void run() {
            super.run();
            mTimer = new Timer(true);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if ((System.currentTimeMillis() - lastSyncTime) > 5 * 1000) {
                        IntentUtils.stopServerReceiverService(FaceTransApplication.getFaceTransApplicationContext());
                    }
                }
            };
            mTimer.schedule(timerTask, 5 * 1000, 3 * 1000);
        }

        public void updateSyncTime(long time) {
            lastSyncTime = time;
        }

        public void stopSyncTime() {
            mTimer.cancel();
        }
    }

    public void release() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                releaseSocket();
                releaseThread();
                if (null != mServerInnerSyncThread) mServerInnerSyncThread.stopSyncTime();
            }
        }).start();
    }

    private void releaseSocket() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopReceiverThread() {
        if (mSendThread != null) {
            mSendThread.stopSendThread();
        }
        if (mReceiveThread != null) {
            mReceiveThread.stopReceiveThread();
        }
    }

    private void releaseThread() {
        if (mReceiveThread != null) {
            mReceiveThread.setIsContinue(false);
            mReceiveThread.closeStream();
        }
        if (mSendThread != null) {
            mSendThread.setIsContinue(false);
            mSendThread.closeStream();
        }
        if (mExecute != null) {
            if (!mExecute.isShutdown()) {
                mExecute.shutdownNow();
            }
        }
    }
}
