package org.hobart.facetrans.socket.transfer.thread;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.SocketConnectEvent;
import org.hobart.facetrans.event.SocketTransferEvent;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.transfer.TransferDataQueue;
import org.hobart.facetrans.socket.transfer.TransferProtocol;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 发送线程
 * Created by huzeyin on 2017/11/24.
 */

public class SendRunnable implements Runnable {

    private static final String LOG_PREFIX = "Socket Send :";

    private volatile boolean isContinue = true;

    private Socket mSocket;

    private DataOutputStream mOutputStream;

    private BufferedInputStream mInputStream;

    private int mTransferStatus = TransferStatus.TRANSFERING;

    private TransferModel mCurrentTransferModel;

    private long totalSize;

    private SendThread mSendThread;

    private Gson mGson;

    public SendRunnable(Socket socket) {

        mSocket = socket;

        mGson = new Gson();
    }

    @Override
    public void run() {
        try {
            mOutputStream = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));

            while (isContinue) {
                if (null == mSocket || mSocket.isClosed() || mOutputStream == null) {
                    continue;
                }

                TransferProtocol transferProtocol = null;
                try {
                    transferProtocol = TransferDataQueue.getInstance().take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (null == transferProtocol) {
                    continue;
                }

                LogcatUtils.d(LOG_PREFIX + " run transferProtocol 发送类型:" + transferProtocol.type);

                if (transferProtocol.type == TransferProtocol.TYPE_ACK) {

                    LogcatUtils.d(LOG_PREFIX + " run transferProtocol 发送握手信号:" + transferProtocol.toString());

                    sendTextData(transferProtocol, false);

                } else if (transferProtocol.type == TransferProtocol.TYPE_CONFIRM_ACK) {

                    LogcatUtils.d(LOG_PREFIX + " run transferProtocol 发送确认握手信号:" + transferProtocol.toString());

                    sendTextData(transferProtocol, false);

                } else if (transferProtocol.type == TransferProtocol.TYPE_DISCONNECT) {

                    LogcatUtils.d(LOG_PREFIX + " run transferProtocol 发送主动断开连接信号:" + transferProtocol.toString());

                    sendTextData(transferProtocol, false);

                    poseDisconnectEvent();

                } else if (transferProtocol.type == TransferProtocol.TYPE_MISS_MATCH) {

                    LogcatUtils.d(LOG_PREFIX + " run transferProtocol 发送Wi-Fi不匹配信号:" + transferProtocol.toString());

                    sendTextData(transferProtocol, false);

                } else if (transferProtocol.type == TransferProtocol.TYPE_DATA_TRANSFER) {

                    LogcatUtils.d(LOG_PREFIX + " run transferProtocol 开始发送数据:" + transferProtocol.toString());

                    TransferModel transferBean = transferProtocol.transferData;

                    switch (transferBean.type) {
                        case TransferModel.TYPE_APK:
                        case TransferModel.TYPE_FILE:
                        case TransferModel.TYPE_IMAGE:
                        case TransferModel.TYPE_MUSIC:
                        case TransferModel.TYPE_VIDEO:
                            sendFile(transferProtocol);
                            break;
                        case TransferModel.TYPE_TRANSFER_DATA_LIST:

                            LogcatUtils.d(LOG_PREFIX + " run transferProtocol 开始发送传输列表数据");

                            sendTextData(transferProtocol, true);

                            break;
                        case TransferModel.TYPE_FOLDER:
                            break;
                        default:
                            continue;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
//            postIOException();
        }
    }



    private void poseDisconnectEvent() {
        SocketConnectEvent event = new SocketConnectEvent();
        event.status = SocketConnectEvent.DIS_CONNECTED;
        EventBus.getDefault().post(event);
    }

    private void postIOException() {
        SocketTransferEvent event = new SocketTransferEvent();
        event.connectStatus = SocketTransferEvent.SOCKET_CONNECT_FAILURE;
        EventBus.getDefault().post(event);
    }

    private void sendTextData(TransferProtocol transferProtocol, boolean notifyUI) {
        try {
            mOutputStream.writeUTF(mGson.toJson(transferProtocol));
            mOutputStream.flush();
            if (notifyUI)
                postSuccess(transferProtocol);
        } catch (IOException e) {
            e.printStackTrace();
//            postIOException();
        }
    }

    private void postSuccess(TransferProtocol transferProtocol) {
        SocketTransferEvent event = new SocketTransferEvent();
        if (transferProtocol.type == TransferProtocol.TYPE_DATA_TRANSFER) {
            event.transferData = transferProtocol.transferData;
            event.transferData.transferStatus = TransferStatus.TRANSFER_SUCCESS;
        }
        event.type = transferProtocol.type;
        event.ssid = transferProtocol.ssid;
        event.ssm = transferProtocol.ssm;
        EventBus.getDefault().post(event);
    }

    private void sendFile(TransferProtocol transferProtocol) {

        if (null == mOutputStream || null == transferProtocol)
            return;

        File file;
        byte[] buffer = null;
        reset();

        String filePath = transferProtocol.transferData.filePath;

        if (FileUtils.isFileExist(filePath)) {

            file = new File(filePath);
            try {

                sendTextData(transferProtocol, false);

                mInputStream = new BufferedInputStream(new FileInputStream(file));


                mCurrentTransferModel = transferProtocol.transferData;

                long fileSize = transferProtocol.transferData.fileSize;

                LogcatUtils.d(LOG_PREFIX + "发送文件 大小：" + fileSize);

                String id = transferProtocol.transferData.id;

                LogcatUtils.d(LOG_PREFIX + "发送文件 编号：" + id);

                int read = 0;

                buffer = new byte[SocketConstants.TCP_BUFFER_SIZE];
                mSendThread = new SendThread();
                mSendThread.start();
                while (null != mSendThread && mSendThread.monitor && (read = mInputStream.read(buffer, 0, buffer.length)) != -1) {

                    mOutputStream.write(buffer, 0, read);
                    totalSize += read;

                    if (totalSize < fileSize) {

                        mTransferStatus = TransferStatus.TRANSFERING;

                    } else {

                        postFileFinishEvent(TransferStatus.TRANSFER_SUCCESS);
                    }
                }
                mOutputStream.flush();
                mInputStream.close();
            } catch (IOException e) {
                postFileFinishEvent(TransferStatus.TRANSFER_FAILED);
//                postIOException();
                e.printStackTrace();
            } finally {
                if (null != mSendThread)
                    mSendThread.setMonitor(false);
            }
        }
    }

    private void reset() {
        mCurrentTransferModel = null;
        totalSize = 0;
    }

    public void setIsContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    private final class SendThread extends Thread {

        boolean monitor = true;

        @Override
        public void run() {
            super.run();
            while (monitor) {
                try {
                    postSendFileInfo();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setMonitor(boolean monitor) {
            this.monitor = monitor;
        }
    }

    int getSendProgress(long totalSize, long currentSize) {
        if (totalSize <= 0) return 0;
        return (int) (((float) currentSize / totalSize) * 100);
    }

    private void postSendFileInfo() {
        int progress = getSendProgress(mCurrentTransferModel.fileSize, totalSize);
        SocketTransferEvent event = new SocketTransferEvent();
        event.type = TransferProtocol.TYPE_DATA_TRANSFER;
        event.transferData = mCurrentTransferModel;
        event.transferData.transferStatus = mTransferStatus;
        event.transferData.progress = progress;
        event.transferData.id = mCurrentTransferModel.id;
        event.transferData.mode = TransferModel.OPERATION_MODE_SEND;
        EventBus.getDefault().post(event);
    }

    void postFileFinishEvent(int status) {
        if (mSendThread != null) {
            mSendThread.setMonitor(false);
        }
        mTransferStatus = status;
        postSendFileInfo();
    }

    public void stopSendThread() {
        if (mSendThread != null) {
            mSendThread.setMonitor(false);
        }
    }

    public void closeStream() {
        try {
            if (mOutputStream != null) {
                mOutputStream.close();
            }
            if (mInputStream != null) {
                mInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
