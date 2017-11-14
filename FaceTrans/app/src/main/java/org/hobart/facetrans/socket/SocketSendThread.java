package org.hobart.facetrans.socket;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketSendThread implements Runnable {
    @Override
    public void run() {

    }
    //    private static final String TAG = SocketSendThread.class.getSimpleName();
//    DataOutputStream out = null;
//    BufferedInputStream bis = null;
//    Socket mSocket;
//    Context mContext;
//    long totoleSize = 0;
//    long fileSize = 0;
//    String fileName = null;
//    String id = null;
//    int current_transfer_status = GameTransferStuatusBean.TRANSFERING;
//    SendThread thread;
//    boolean mMonitor = true;
//    String filePath=null;
//
//    public SocketSendThread(Socket mSocket, Context context) {
//        this.mSocket = mSocket;
//        this.mContext = context;
//        Log.d(TAG, "SocketReceiverService: ");
//    }
//
//    @Override
//    public void run() {
//        try {
//            out = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
//            //out=new DataOutputStream(mSocket.getOutputStream());
//            while (mMonitor) {
//
//                if (mSocket == null || out == null || mSocket.isClosed()) {
//                    continue;
//                }
//                SocketRequestBean requestBean = RequestQueneManager.getInstance().poll();
//                if (requestBean == null) {
//                    continue;
//                }
//                if (requestBean instanceof SocketTextRequestBean) {
//                    sendText((SocketTextRequestBean) requestBean);
//                } else if (requestBean instanceof SocketFileRequestBean) {
//                    sendFile((SocketFileRequestBean) requestBean);
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    //发送字符
//    void sendText(SocketTextRequestBean requestBean) {
//        String content = requestBean.getContent();
//        if (out == null || requestBean == null || StringUtils.isEmpty(content)) {
//            return;
//        }
//        Log.d(TAG, "receiverText: 开始发送文本信息");
//        try {
//            if(!content.equals(BaseSocketEvent.SOCKET_HEART_BEAT)&&!content.equals(BaseSocketEvent.SOCKET_DEVICE_MODLE_HEAD)){
//                Log.d(TAG, "receiverText: msg");
//            }
//            out.writeInt(SocketRequestBean.TEXT_TYPE);
//            out.flush();
//            out.writeUTF(content);
//            out.flush();
//            Log.d(TAG, "receiverText: 发送的文本数据为" + content);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    //发送文件
//    void sendFile(SocketFileRequestBean requestBean) {
//        if (out == null || requestBean == null) {
//            return;
//        }
//        File file = null;
//        byte[] buf = null;
//        totoleSize = 0;
//        fileSize = 0;
//        fileName = null;
//        filePath = requestBean.getFilePath();
//        id = null;
//        if (FileUtil.isFileExist(filePath)) {
//            file = new File(filePath);
//            try {
//                bis = new BufferedInputStream(new FileInputStream(file));
//                out.writeInt(SocketRequestBean.FILE_TYPE);
//                out.flush();
//                fileSize = requestBean.getFileSize();
//                out.writeLong(fileSize);
//                out.flush();
//                fileName = requestBean.getFileName();
//                out.writeUTF(fileName);
//                out.flush();
//                id = requestBean.getId();
//                out.writeUTF(id);
//                out.flush();
//                int read = 0;
//                buf = new byte[SocketConfig.TCP_BUFFER_SIZE];
//                thread = new SendThread();
//                thread.start();
//                while ((read = bis.read(buf, 0, buf.length)) != -1) {
//                    out.write(buf, 0, read);
//                    totoleSize += read;
//                    if (totoleSize < fileSize) {
//                        current_transfer_status = GameTransferStuatusBean.TRANSFERING;
//                    } else {
//                        postFileFinishEvent(GameTransferStuatusBean.TRANSFER_SUCCESS);
//                    }
//                }
//                out.flush();
//                bis.close();
//            } catch (Exception e) {
//                postFileFinishEvent(GameTransferStuatusBean.TRANSFER_FAILED);
//                e.printStackTrace();
//            } finally {
//                if (thread != null) {
//                    thread.setMonitor(false);
//                }
//            }
//        }
//    }
//
//
//    //获得当前传输的进度
//    int getSendProgress(long totoleSize, long currentSize) {
//        int progress = 0;
//        progress = (int) (((float) currentSize / totoleSize) * 100);
//        Log.d(TAG, "getReceiverProgress: " + progress + ":::size::::" + currentSize);
//        return progress;
//    }
//
//    //发送文件的信息
//    void postSendFileInfo() {
//        int progress = 0;
//        progress = getSendProgress(fileSize, totoleSize);
//        SocketFileEvent event = new SocketFileEvent();
//        event.progress = progress;
//        event.fileName = fileName;
//        event.fileSavePath=filePath;
//        event.status = current_transfer_status;
//        event.type = BaseSocketEvent.TYPE_FILE;
//        event.id = id;
//        event.mode = BaseSocketEvent.OPERATION_MODE_SEND;
//        if (current_transfer_status == GameTransferStuatusBean.TRANSFER_SUCCESS
//                || current_transfer_status == GameTransferStuatusBean.TRANSFER_FAILED) {
//            if (thread != null) {
//                if (thread.monitor) {
//                    return;
//                }
//            }
//        }
//        EventBus.getDefault().post(event);
//    }
//
//
//    class SendThread extends Thread {
//        volatile boolean monitor = true;
//
//        public void run() {
//            super.run();
//            while (monitor) {
//                try {
//                    postSendFileInfo();
//                    sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void setMonitor(boolean monitor) {
//            this.monitor = monitor;
//        }
//
//    }
//
//
//    public void setMonitor(boolean monitor) {
//        this.mMonitor = monitor;
//    }
//
//
//    //文件最后的状态，成功、失败
//    void postFileFinishEvent(int status) {
//        if (thread != null) {
//            thread.setMonitor(false);
//        }
//        current_transfer_status = status;
//        postSendFileInfo();
//    }
//
//    //关闭流
//    public void closeStreame() {
//        try {
//            if (out != null) {
//                out.close();
//            }
//            if (bis != null) {
//                bis.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
