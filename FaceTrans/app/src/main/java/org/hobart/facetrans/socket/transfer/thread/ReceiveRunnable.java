package org.hobart.facetrans.socket.transfer.thread;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.event.SocketSyncEvent;
import org.hobart.facetrans.event.SocketTransferEvent;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.transfer.TransferProtocol;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.LogcatUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 接收线程
 * Created by huzeyin on 2017/11/24.
 */

public class ReceiveRunnable implements Runnable {

    private static final String LOG_PREFIX = "Socket Receive :";

    private volatile boolean isContinue = true;
    private Socket mSocket;
    private DataInputStream mInputStream;
    private BufferedOutputStream mOutputStream;
    private ReceiveThread mReceiveThread;
    private int mCurrentTransferStatus = TransferStatus.WAITING;

    private TransferProtocol mCurrentTransferProtocol;

    private long totalSize;

    private int bytesRead = 0;

    private Gson mGson;


    public ReceiveRunnable(Socket socket) {
        this.mSocket = socket;
        mGson = new Gson();
    }

    @Override
    public void run() {

        try {

            LogcatUtils.d(LOG_PREFIX + "接收大小 " + mSocket.getReceiveBufferSize());

            mInputStream = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));

            while (isContinue) {

                if (null == mSocket || mSocket.isClosed() || mInputStream == null) {
                    continue;
                }

                String protocol = mInputStream.readUTF();

                if (TextUtils.isEmpty(protocol)) continue;

                TransferProtocol transferProtocol = null;
                try {
                    transferProtocol = mGson.fromJson(protocol, TransferProtocol.class);
                    if (null == transferProtocol) continue;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                postSocketSyncEvent(SocketSyncEvent.SYNCING);

                if (transferProtocol.type == TransferProtocol.TYPE_ACK) {

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_ACK 信号");

                    postReceiveProtocol(transferProtocol, TransferStatus.TRANSFER_SUCCESS);

                } else if (transferProtocol.type == TransferProtocol.TYPE_CONFIRM_ACK) {

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_CONFIRM_ACK 信号");

                    postReceiveProtocol(transferProtocol, TransferStatus.TRANSFER_SUCCESS);


                } else if (transferProtocol.type == TransferProtocol.TYPE_DISCONNECT) {

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_DISCONNECT 信号");

                    postReceiveProtocol(transferProtocol, TransferStatus.TRANSFER_SUCCESS);

                } else if (transferProtocol.type == TransferProtocol.TYPE_MISS_MATCH) {

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_MISS_MATCH 信号");

                    postReceiveProtocol(transferProtocol, TransferStatus.TRANSFER_SUCCESS);

                } else if (transferProtocol.type == TransferProtocol.TYPE_DATA_TRANSFER) {

                    TransferModel transferBean = transferProtocol.transferData;

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_DATA_TRANSFER 信号");

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_DATA_TRANSFER 信号 transferBean value :" + transferBean);

                    switch (transferBean.type) {
                        case TransferModel.TYPE_APK:
                        case TransferModel.TYPE_FILE:
                        case TransferModel.TYPE_IMAGE:
                        case TransferModel.TYPE_MUSIC:
                        case TransferModel.TYPE_VIDEO:
                            receiveFile(transferProtocol, mInputStream);
                            break;
                        case TransferModel.TYPE_TRANSFER_DATA_LIST:

                            LogcatUtils.d(LOG_PREFIX + " run receive data  接收数据列表");

                            postReceiveProtocol(transferProtocol, TransferStatus.TRANSFER_SUCCESS);

                            break;
                        case TransferModel.TYPE_FOLDER:
                            //TODO:
                            break;
                        default:
                            continue;
                    }
                } else if (transferProtocol.type == TransferProtocol.TYPE_TYPE_DATA_TRANSFER_FINISH) {

                    LogcatUtils.d(LOG_PREFIX + " run receive data  接收 TYPE_TYPE_DATA_TRANSFER_FINISH 信号");

                    postSocketSyncEvent(SocketSyncEvent.FINISH);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            postIOException();
        }
    }

    private void postSocketSyncEvent(int flag) {
        SocketSyncEvent event = new SocketSyncEvent();
        event.flag = flag;
        EventBus.getDefault().post(event);
    }

    private void postIOException() {
        SocketTransferEvent event = new SocketTransferEvent();
        event.connectStatus = SocketTransferEvent.SOCKET_CONNECT_FAILURE;
        EventBus.getDefault().post(event);
        if (null != mCurrentTransferProtocol) {
            if (mCurrentTransferStatus == TransferStatus.TRANSFER_SUCCESS || mCurrentTransferStatus == TransferStatus.FINISH) {
                return;
            }
            FileUtils.deleteFile(mCurrentTransferProtocol.transferData.savePath);
            postFileFinishEvent(TransferStatus.TRANSFER_FAILED);
        }
    }

    private void postReceiveProtocol(TransferProtocol transferProtocol, int status) {
        SocketTransferEvent event = new SocketTransferEvent();
        if (transferProtocol.type == TransferProtocol.TYPE_DATA_TRANSFER) {
            event.transferData = transferProtocol.transferData;
            event.transferData.mode = TransferModel.OPERATION_MODE_RECEIVER;
            event.transferData.transferStatus = status;
        }
        event.type = transferProtocol.type;
        event.ssid = transferProtocol.ssid;
        event.ssm = transferProtocol.ssm;
        EventBus.getDefault().post(event);

        if (transferProtocol.type == TransferProtocol.TYPE_DISCONNECT) {
            if (null != mCurrentTransferProtocol) {
                FileUtils.deleteFile(mCurrentTransferProtocol.transferData.savePath);
                postFileFinishEvent(TransferStatus.TRANSFER_FAILED);
            }
        }
    }

    private void receiveFile(TransferProtocol transferProtocol, DataInputStream inputStream) {

        if (null == inputStream) return;

        int byteSize = SocketConstants.TCP_BUFFER_SIZE;
        byte[] buffer = new byte[byteSize];
        reset();

        try {

            long fileSize = transferProtocol.transferData.fileSize;

            LogcatUtils.d(LOG_PREFIX + "接收的文件大小 " + fileSize);

            String fileName = transferProtocol.transferData.fileName;

            LogcatUtils.d(LOG_PREFIX + "接收的文件名称 " + fileName);

            String id = transferProtocol.transferData.id;

            LogcatUtils.d(LOG_PREFIX + "接收的文件编号 " + id);

            String savePath = GlobalConfig.getTransferDirectory() + File.separator + fileName;
            if (!FileUtils.isFolderExist(savePath)) {
                FileUtils.makeDirs(savePath);
            }

            transferProtocol.transferData.savePath = savePath;

            mCurrentTransferProtocol = transferProtocol;

            postReceiveProtocol(transferProtocol, mCurrentTransferStatus);

            LogcatUtils.d(LOG_PREFIX + "接收的文件保存路径 " + savePath);

            mOutputStream = new BufferedOutputStream(new FileOutputStream(new File(savePath)));
            mReceiveThread = new ReceiveThread();
            mReceiveThread.start();
            while (null != mReceiveThread && mReceiveThread.monitor) {

                if ((totalSize + byteSize) > fileSize) {
                    int lastSize = (int) (fileSize - totalSize);
                    bytesRead = inputStream.read(buffer, 0, lastSize);

                    postSocketSyncEvent(SocketSyncEvent.SYNCING);

                    if (bytesRead != -1) {
                        mOutputStream.write(buffer, 0, bytesRead);
                        totalSize += bytesRead;

                        LogcatUtils.d(LOG_PREFIX + "接收了多少 " + bytesRead + "----bufferSize-----");

                        if (fileSize > totalSize) {
                            mCurrentTransferStatus = TransferStatus.TRANSFERING;
                        } else {

                            LogcatUtils.d(LOG_PREFIX + "接收了完成 ");

                            postFileFinishEvent(TransferStatus.TRANSFER_SUCCESS);
                            break;
                        }
                    } else {

                        LogcatUtils.d(LOG_PREFIX + "没有什么可以传输的了 ");

                        postFileFinishEvent(TransferStatus.TRANSFER_FAILED);
                        break;
                    }
                } else {

                    postSocketSyncEvent(SocketSyncEvent.SYNCING);

                    bytesRead = inputStream.read(buffer, 0, buffer.length);
                    if (bytesRead != -1) {

                        mOutputStream.write(buffer, 0, bytesRead);
                        totalSize += bytesRead;

                        LogcatUtils.d(LOG_PREFIX + "接收了多少 " + bytesRead + "----bufferSize-----" + buffer.length);

                        if (fileSize > totalSize) {
                            mCurrentTransferStatus = TransferStatus.TRANSFERING;
                        } else {
                            postFileFinishEvent(TransferStatus.TRANSFER_SUCCESS);
                            break;
                        }
                    } else {

                        LogcatUtils.d(LOG_PREFIX + "没有什么可以传输的了 ");

                        postFileFinishEvent(TransferStatus.TRANSFER_FAILED);
                        break;
                    }
                }
            }

            LogcatUtils.d(LOG_PREFIX + "数据接收完成 ");

            mOutputStream.flush();

        } catch (IOException e) {
            FileUtils.deleteFile(mCurrentTransferProtocol.transferData.savePath);
            postFileFinishEvent(TransferStatus.TRANSFER_FAILED);
        } finally {
            if (null != mReceiveThread)
                mReceiveThread.setMonitor(false);
            try {
                if (null != mOutputStream)
                    mOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread {

        volatile boolean monitor = true;

        @Override
        public void run() {
            super.run();
            while (monitor) {
                try {
                    postReceiverFileInfo();
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setMonitor(boolean monitor) {
            this.monitor = monitor;
        }
    }

    int getReceiveProgress(long totalSize, long currentSize) {
        if (totalSize == 0) return 0;
        return (int) (((float) currentSize / totalSize) * 100);
    }

    private void postReceiverFileInfo() {
        int progress = getReceiveProgress(mCurrentTransferProtocol.transferData.fileSize, totalSize);
        SocketTransferEvent event = new SocketTransferEvent();
        event.type = TransferProtocol.TYPE_DATA_TRANSFER;
        event.transferData = mCurrentTransferProtocol.transferData;
        event.transferData.transferStatus = mCurrentTransferStatus;
        event.transferData.progress = progress;
        event.transferData.id = mCurrentTransferProtocol.transferData.id;
        event.transferData.mode = TransferModel.OPERATION_MODE_RECEIVER;
        EventBus.getDefault().post(event);
    }

    private void postFileFinishEvent(int status) {
        if (mReceiveThread != null)
            mReceiveThread.setMonitor(false);
        mCurrentTransferStatus = status;
        postReceiverFileInfo();
    }

    public void stopReceiveThread() {
        if (mReceiveThread != null)
            mReceiveThread.setMonitor(false);
    }

    private void reset() {
        mCurrentTransferProtocol = null;
        bytesRead = 0;
        totalSize = 0;
    }

    public void setIsContinue(boolean isContinue) {
        this.isContinue = isContinue;
    }

    public void closeStream() {
        try {
            if (null != mInputStream)
                mInputStream.close();
            if (null != mOutputStream)
                mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
