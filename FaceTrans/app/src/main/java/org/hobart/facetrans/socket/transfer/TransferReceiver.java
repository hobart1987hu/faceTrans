package org.hobart.facetrans.socket.transfer;

import java.io.IOException;
import java.net.Socket;
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
        }
    }

    public void release() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                releaseSocket();
                releaseThread();
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

    private void releaseThread() {
        if (mReceiveThread != null) {
            mReceiveThread.setIsContinue(false);
            mReceiveThread.closeStream();
        }
        if (mSendThread != null) {
            mSendThread.setIsContinue(false);
            mSendThread.closeStreame();
        }
        if (mExecute != null) {
            if (!mExecute.isShutdown()) {
                mExecute.shutdownNow();
            }
        }
    }
}
