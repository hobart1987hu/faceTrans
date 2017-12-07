package org.hobart.facetrans.socket.transfer;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.event.SocketEvent;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.SocketConstants;
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
    private long totalSize = 0;
    private long fileSize = 0;
    private int type;
    private String savePath = null;
    private String fileName;
    private String id;
    private int bytesRead = 0;

    public ReceiveRunnable(Socket socket) {
        this.mSocket = socket;
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

                type = mInputStream.readInt();

                LogcatUtils.d(LOG_PREFIX + "接收文件类型 " + type);

                switch (type) {
                    case TransferModel.TYPE_APK:
                    case TransferModel.TYPE_FILE:
                    case TransferModel.TYPE_IMAGE:
                    case TransferModel.TYPE_MUSIC:
                    case TransferModel.TYPE_VIDEO:
                        receiveFile(mInputStream);
                        break;
                    case TransferModel.TYPE_HEART_BEAT:
                        receiveHearBeat(mInputStream);
                        break;
                    case TransferModel.TYPE_TRANSFER_DATA_LIST:
                        receiveTransferDataList(mInputStream);
                        break;
                    case TransferModel.TYPE_FOLDER:
                        //TODO:
                        break;
                    default:
                        continue;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收心跳
     *
     * @param inputStream
     */
    private void receiveHearBeat(DataInputStream inputStream) {

        if (null == inputStream) return;
        try {
            String content = inputStream.readUTF();

            LogcatUtils.d(LOG_PREFIX + "接收的心跳内容是： " + content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收数据传输列表
     *
     * @param inputStream
     */
    private void receiveTransferDataList(DataInputStream inputStream) {

        if (null == inputStream) return;
        try {
            String content = inputStream.readUTF();

            LogcatUtils.d(LOG_PREFIX + "接收的数据传输列表是 " + content);

            postReceiveText(content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveFile(DataInputStream inputStream) {

        if (null == inputStream) return;

        int byteSize = SocketConstants.TCP_BUFFER_SIZE;
        byte[] buffer = new byte[byteSize];
        reset();

        try {

            fileSize = inputStream.readLong();

            LogcatUtils.d(LOG_PREFIX + "接收的文件大小 " + fileSize);

            fileName = inputStream.readUTF();

            LogcatUtils.d(LOG_PREFIX + "接收的文件名称 " + fileName);

            id = inputStream.readUTF();

            LogcatUtils.d(LOG_PREFIX + "接收的文件编号 " + id);

            savePath = GlobalConfig.getTransferDirectory() + File.separator + fileName;
            if (!FileUtils.isFolderExist(savePath)) {
                FileUtils.makeDirs(savePath);
            }

            LogcatUtils.d(LOG_PREFIX + "接收的文件保存路径 " + savePath);

            mOutputStream = new BufferedOutputStream(new FileOutputStream(new File(savePath)));
            mReceiveThread = new ReceiveThread();
            mReceiveThread.start();
            while (isContinue) {

                if ((totalSize + byteSize) > fileSize) {
                    int lastSize = (int) (fileSize - totalSize);
                    bytesRead = inputStream.read(buffer, 0, lastSize);
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
            FileUtils.deleteFile(savePath);
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
                    sleep(1000);
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
        int progress = getReceiveProgress(fileSize, totalSize);
        SocketEvent event = new SocketEvent(type, mCurrentTransferStatus, SocketEvent.OPERATION_MODE_RECEIVER);
        event.progress = progress;
        event.fileName = fileName;
        event.id = id;
        event.filePath = savePath;
        if (mCurrentTransferStatus == TransferStatus.TRANSFER_SUCCESS
                || mCurrentTransferStatus == TransferStatus.TRANSFER_FAILED) {
            if (mReceiveThread != null) {
                if (mReceiveThread.monitor) {
                    return;
                }
            }
        }
        EventBus.getDefault().post(event);
    }

    private void postFileFinishEvent(int status) {
        if (mReceiveThread != null)
            mReceiveThread.setMonitor(false);
        mCurrentTransferStatus = status;
        postReceiverFileInfo();
    }

    private void postReceiveText(String content) {
        SocketEvent event = new SocketEvent(type, TransferStatus.TRANSFER_SUCCESS, SocketEvent.OPERATION_MODE_RECEIVER);
        event.content = content;
        EventBus.getDefault().post(event);
    }

    private void reset() {
        totalSize = 0;
        fileSize = 0;
        savePath = null;
        fileName = null;
        id = null;
        bytesRead = 0;
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
