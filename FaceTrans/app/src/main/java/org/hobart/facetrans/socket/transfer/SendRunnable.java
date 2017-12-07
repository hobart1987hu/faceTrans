package org.hobart.facetrans.socket.transfer;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.SocketEvent;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.SocketConstants;
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

    private long totalSize;

    private long fileSize;

    private int type;

    private String fileName;

    private int mTransferStatus = TransferStatus.TRANSFERING;

    private String filePath;

    private String id;

    private SendThread mSendThread;

    public SendRunnable(Socket socket) {

        mSocket = socket;
    }

    @Override
    public void run() {
        try {
            mOutputStream = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));

            while (isContinue) {
                if (null == mSocket || mSocket.isClosed() || mOutputStream == null) {
                    continue;
                }

                TransferModel transferBean = null;
                try {
                    transferBean = SocketTransferQueue.getInstance().take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (null == transferBean) {
                    continue;
                }

                LogcatUtils.d(LOG_PREFIX + " run transferBean:" + transferBean.toString());

                switch (transferBean.type) {
                    case TransferModel.TYPE_APK:
                    case TransferModel.TYPE_FILE:
                    case TransferModel.TYPE_IMAGE:
                    case TransferModel.TYPE_MUSIC:
                    case TransferModel.TYPE_VIDEO:
                        sendFile(transferBean);
                        break;
                    case TransferModel.TYPE_HEART_BEAT:
                        sendHearBeat(transferBean);
                        break;
                    case TransferModel.TYPE_TRANSFER_DATA_LIST:
                        sendTransferDataList(transferBean);
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
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {

        }
    }

    /**
     * 发送心跳包，这个不需要回调给ui界面
     */
    private void sendHearBeat(TransferModel transferBean) {

        if (null == mOutputStream || null == transferBean || TextUtils.isEmpty(transferBean.content))
            return;

        String content = transferBean.content;

        try {
            LogcatUtils.d(LOG_PREFIX + " 开始发送心跳包" + content);
            //先发送数据类型
            mOutputStream.writeInt(transferBean.type);
            mOutputStream.flush();
            //在发送文本数据
            mOutputStream.writeUTF(content);
            mOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送需要传输的数据列表
     *
     * @param textTransferBean
     */
    private void sendTransferDataList(TransferModel textTransferBean) {

        if (null == mOutputStream || null == textTransferBean || TextUtils.isEmpty(textTransferBean.content))
            return;

        String content = textTransferBean.content;

        try {

            LogcatUtils.d(LOG_PREFIX + " 开始发送传输的数据列表" + content);

            mOutputStream.writeInt(textTransferBean.type);
            mOutputStream.flush();
            mOutputStream.writeUTF(content);
            mOutputStream.flush();

            postSendText(textTransferBean);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postSendText(TransferModel transModel) {
        SocketEvent event = new SocketEvent(transModel.type, TransferStatus.TRANSFER_SUCCESS, TransferModel.OPERATION_MODE_SEND);
        EventBus.getDefault().post(event);
    }

    private void sendFile(TransferModel fileTransferBean) {

        if (null == mOutputStream || null == fileTransferBean)
            return;

        LogcatUtils.d(LOG_PREFIX + " 开始发送文件 type:" + fileTransferBean.type);

        File file;
        byte[] buffer = null;
        reset();
        filePath = fileTransferBean.filePath;

        if (FileUtils.isFileExist(filePath)) {

            file = new File(filePath);
            try {
                mInputStream = new BufferedInputStream(new FileInputStream(file));

                type = fileTransferBean.type;
                mOutputStream.writeInt(type);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 类型：" + type);

                fileSize = fileTransferBean.fileSize;
                mOutputStream.writeLong(fileSize);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 大小：" + fileSize);

                fileName = fileTransferBean.fileName;
                mOutputStream.writeUTF(fileName);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 名称：" + fileName);

                id = fileTransferBean.id;
                mOutputStream.writeUTF(id);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 编号：" + id);

                int read = 0;

                buffer = new byte[SocketConstants.TCP_BUFFER_SIZE];
                mSendThread = new SendThread();
                mSendThread.start();
                while ((read = mInputStream.read(buffer, 0, buffer.length)) != -1) {

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
                e.printStackTrace();
            } finally {
                if (null != mSendThread)
                    mSendThread.setMonitor(false);
            }
        }
    }

    private void reset() {
        totalSize = 0;
        fileSize = 0;
        fileName = null;
        filePath = null;
        id = null;
        type = -1;
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
                    Thread.sleep(1000);
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
        int progress = getSendProgress(fileSize, totalSize);
        SocketEvent event = new SocketEvent(type, mTransferStatus, SocketEvent.OPERATION_MODE_SEND);
        event.progress = progress;
        event.fileName = fileName;
        event.filePath = filePath;
        event.id = id;
        if (mTransferStatus == TransferStatus.TRANSFER_SUCCESS
                || mTransferStatus == TransferStatus.TRANSFER_FAILED) {
            if (mSendThread != null) {
                if (mSendThread.monitor) {
                    return;
                }
            }
        }
        EventBus.getDefault().post(event);
    }


    void postFileFinishEvent(int status) {
        if (mSendThread != null) {
            mSendThread.setMonitor(false);
        }
        mTransferStatus = status;
        postSendFileInfo();
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
