package org.hobart.facetrans.socket.transfer;

import org.hobart.facetrans.socket.transfer.thread.ReceiveRunnable;
import org.hobart.facetrans.socket.transfer.thread.SendRunnable;

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

    public TransferSender(Socket socket) {
        mSocket = socket;
        mExecute = Executors.newCachedThreadPool();
        startSend();
    }

    private void startSend() {
        if (mSocket != null && mSocket.isConnected()) {
            mSendThread = new SendRunnable(mSocket);
            mReceiveThread = new ReceiveRunnable(mSocket);
            mExecute.execute(mReceiveThread);
            mExecute.execute(mSendThread);
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

    private void releaseService() {
        if (mSendThread != null) {
            mSendThread.setIsContinue(false);
            mSendThread.closeStream();
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
                releaseService();
                releaseSocket();
            }
        }).start();
    }
}
