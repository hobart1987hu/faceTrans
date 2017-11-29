package org.hobart.facetrans.socket.transfer;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.event.SocketEvent;
import org.hobart.facetrans.event.SocketFileEvent;
import org.hobart.facetrans.event.SocketTextEvent;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.transfer.model.FileTransModel;
import org.hobart.facetrans.socket.transfer.model.TextTransModel;
import org.hobart.facetrans.socket.transfer.model.TransModel;
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

    private boolean isZipFile;

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

                TransModel transferBean = null;
                try {
                    transferBean = SocketTransferQueue.getInstance().take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (null == transferBean) {
                    continue;
                }

                if (transferBean instanceof TextTransModel) {

                    sendText((TextTransModel) transferBean);

                } else if (transferBean instanceof FileTransModel) {

                    sendFile((FileTransModel) transferBean);
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

    private void sendText(TextTransModel textTransferBean) {

        if (null == mOutputStream || null == textTransferBean || TextUtils.isEmpty(textTransferBean.getContent()))
            return;

        String content = textTransferBean.getContent();

        try {

            LogcatUtils.d(LOG_PREFIX + " 开始发送文本信息" + content + "type->" + textTransferBean.type);

            mOutputStream.writeInt(textTransferBean.type);
            mOutputStream.flush();
            mOutputStream.writeUTF(content);
            mOutputStream.flush();

            postSendText(textTransferBean);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postSendText(TextTransModel transModel) {
        SocketTextEvent event = new SocketTextEvent(transModel.type, TransferStatus.TRANSFER_SUCCESS, TextTransModel.OPERATION_MODE_SEND);
        event.content = transModel.getContent();
        EventBus.getDefault().post(event);
    }

    private void sendFile(FileTransModel fileTransferBean) {

        if (null == mOutputStream || null == fileTransferBean)
            return;
        File file;
        byte[] buffer = null;
        reset();
        filePath = fileTransferBean.getFilePath();

        if (FileUtils.isFileExist(filePath)) {

            file = new File(filePath);
            try {
                mInputStream = new BufferedInputStream(new FileInputStream(file));

                mOutputStream.writeInt(TransModel.TYPE_FILE);
                mOutputStream.flush();

                isZipFile = fileTransferBean.isZipFile();
                mOutputStream.writeInt(isZipFile ? 1 : 0);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 是否是压缩文件：" + isZipFile);

                fileSize = fileTransferBean.getFileSize();
                mOutputStream.writeLong(fileSize);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 大小：" + fileSize);

                fileName = fileTransferBean.getFileName();
                mOutputStream.writeUTF(fileName);
                mOutputStream.flush();

                LogcatUtils.d(LOG_PREFIX + "发送文件 名称：" + fileName);

                id = fileTransferBean.getId();
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
        isZipFile = false;
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
        SocketFileEvent event = new SocketFileEvent(SocketEvent.TYPE_FILE, mTransferStatus, SocketEvent.OPERATION_MODE_SEND);
        event.progress = progress;
        event.fileName = fileName;
        event.fileSavePath = filePath;
        event.id = id;
        event.isZipFile = isZipFile;
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
