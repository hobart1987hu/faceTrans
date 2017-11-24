package org.hobart.facetrans.socket.transfer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 面传中发送方
 * Created by huzeyin on 2017/11/24.
 */

public class TransferSender {

    private ExecutorService mExecute;

    private Socket mSocket;

    private SendRunnable mSendThread;

    private ReceiveRunnable mReceiveThread;

    private HeartBeatRunnable mHeartBeatThread;

    public TransferSender(Socket socket) {
        mSocket = socket;
        mExecute = Executors.newCachedThreadPool();
        startSend();
    }

    private void startSend() {
        if (mSocket != null && mSocket.isConnected()) {
            mSendThread = new SendRunnable(mSocket);
            mReceiveThread = new ReceiveRunnable(mSocket);
            mHeartBeatThread = new HeartBeatRunnable();
            mExecute.execute(mReceiveThread);
            mExecute.execute(mSendThread);
            mExecute.execute(mHeartBeatThread);
        }
    }


    private final class HeartBeatRunnable implements Runnable {

        volatile boolean monitor = true;

        @Override
        public void run() {
            while (monitor) {
                try {
                    SocketTransferQueue.getInstance().sendHeartMsg();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }

        public void setMonitor(boolean monitor) {
            this.monitor = monitor;
        }
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


    private void stopMonitor() {
        if (mHeartBeatThread != null) {
            mHeartBeatThread.setMonitor(false);
        }
    }


    private void releaseService() {
        if (mSendThread != null) {
            mSendThread.setIsContinue(false);
            mSendThread.closeStreame();
        }

        if (mReceiveThread != null) {
            mReceiveThread.setIsContinue(false);
            mReceiveThread.closeStream();
        }

        if (mExecute != null) {
            mExecute.shutdownNow();
        }
    }

    public void release() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopMonitor();
                releaseService();
                releaseSocket();
            }
        }).start();
    }
}
