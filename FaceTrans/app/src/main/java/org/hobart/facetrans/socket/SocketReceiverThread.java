package org.hobart.facetrans.socket;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketReceiverThread implements  Runnable {
    private static final String TAG = SocketReceiverThread.class.getSimpleName();
    Socket mSocket;
    DataInputStream input = null;
    BufferedOutputStream bufferedOutputStream = null;
    Context mContext;
    long totoleSize = 0;
    long fileSize = 0;
    String savePath = null;
    String fileName = null;
    String id = null;
    int bytesRead = 0;
    int current_transfer_status = GameTransferStuatusBean.WAITING;
    ReceiverThread thread;
    boolean mMonitor = true;

    public SocketReceiverThread(Socket mSocket, Context context) {
        this.mSocket = mSocket;
        this.mContext = context;
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "SocketReceiverService: 接收大小" + mSocket.getReceiveBufferSize());
            input = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
            while (mMonitor) {

                if (mSocket == null || input == null || mSocket.isClosed()) {
                    continue;
                }
                int type = input.readInt();
                // Log.d(TAG, "run: 接收的类型" + type);
                if (type == SocketRequestBean.TEXT_TYPE) {
                    receiverText(input);
                } else if (type == SocketRequestBean.FILE_TYPE) {
                    receiverFile(input);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //接收文本信息
    void receiverText(DataInputStream input) {
        if (input == null) {
            return;
        }
        try {
            String content = input.readUTF();
            postTextInfo(content);
            Log.d(TAG, "receiverText: 接收的文本数据为" + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接收文件
    void receiverFile(DataInputStream input) {
        if (input == null) {
            return;
        }
        int byteSize = SocketConfig.TCP_BUFFER_SIZE;
        byte[] buffer = new byte[byteSize];
        totoleSize = 0;
        fileSize = 0;
        savePath = null;
        fileName = null;
        id = null;
        bytesRead = 0;
        try {
            fileSize = input.readLong();
            Log.d(TAG, "receiverFile: 文件大小 " + fileSize);
            fileName = input.readUTF();
            Log.d(TAG, "receiverFile: 文件保存名称 " + fileName);
            id = input.readUTF();
            Log.d(TAG, "receiverFile: 文件编号 " + id);
            savePath = SDCardPathUtil.getTransferDirectory(mContext) + File.separator + fileName;
            if (!FileUtil.isFolderExist(savePath)) {
                FileUtil.makeDirs(savePath);
            }

            Log.d(TAG, "receiverFile: 文件完整名 " + savePath);
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(savePath)));
            Log.d(TAG, "saveFile: 文件大小：" + fileSize + "::::::文件完整路径：：" + savePath);
            thread = new ReceiverThread();
            thread.start();
            while (mMonitor) {
                if ((totoleSize + byteSize) > fileSize) {
                    int restSize = (int) (fileSize - totoleSize);
                    bytesRead = input.read(buffer, 0, restSize);
                    if (bytesRead != -1) {
                        bufferedOutputStream.write(buffer, 0, bytesRead);
                        totoleSize += bytesRead;
                        Log.d(TAG, "receiverFile 1111: 接收了多少" + bytesRead + "----bufferSize-----");
                        if (fileSize > totoleSize) {
                            current_transfer_status = GameTransferStuatusBean.TRANSFERING;
                        } else {
                            postFileFinishEvent(GameTransferStuatusBean.TRANSFER_SUCCESS);
                            break;
                        }
                    } else {
                        Log.d(TAG, "receiverFile11111: 没有可以传输的");
                        postFileFinishEvent(GameTransferStuatusBean.TRANSFER_FAILED);
                        break;
                    }

                } else {
                    bytesRead = input.read(buffer, 0, buffer.length);
                    if (bytesRead != -1) {
                        bufferedOutputStream.write(buffer, 0, bytesRead);
                        totoleSize += bytesRead;
                        Log.d(TAG, "receiverFile000000: 接收了多少" + bytesRead + "-----bufferSize------" + buffer.length);
                        if (fileSize > totoleSize) {
                            current_transfer_status = GameTransferStuatusBean.TRANSFERING;
                        } else {
                            postFileFinishEvent(GameTransferStuatusBean.TRANSFER_SUCCESS);
                            break;
                        }
                    } else {
                        Log.d(TAG, "receiverFile0000: 没有可以传输的");
                        postFileFinishEvent(GameTransferStuatusBean.TRANSFER_FAILED);
                        break;
                    }
                }
            }
            Log.d(TAG, "接收数据完成: ");
            bufferedOutputStream.flush();
        } catch (IOException e) {
            FileUtil.deleteFile(savePath);
            postFileFinishEvent(GameTransferStuatusBean.TRANSFER_FAILED);
            e.printStackTrace();
        } finally {
            try {
                if (thread != null) {
                    thread.setMonitor(false);
                }
                if (bufferedOutputStream != null) {
                    Log.d(TAG, "关闭流: ");
                    bufferedOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //获得当前传输的进度
    int getReceiverProgress(long totoleSize, long currentSize) {
        int progress = 0;
        progress = (int) (((float) currentSize / totoleSize) * 100);
        Log.d(TAG, "getReceiverProgress: " + progress + ":::size::::" + currentSize);
        return progress;
    }


    void postReceiverFileInfo() {
        int progress = 0;
        progress = getReceiverProgress(fileSize, totoleSize);
        SocketFileEvent event = new SocketFileEvent();
        event.progress = progress;
        event.fileName = fileName;
        event.status = current_transfer_status;
        event.id = id;
        event.fileSavePath = savePath;
        event.type = BaseSocketEvent.TYPE_FILE;
        event.mode = BaseSocketEvent.OPERATION_MODE_RECEIVER;
        if (current_transfer_status == GameTransferStuatusBean.TRANSFER_SUCCESS
                || current_transfer_status == GameTransferStuatusBean.TRANSFER_FAILED) {
            Log.d(TAG, "receiverFile: 接收完成:::" + thread.monitor);
            if (thread != null) {
                if (thread.monitor) {
                    return;
                }
            }
        }
        EventBus.getDefault().post(event);
    }


    class ReceiverThread extends Thread {
        volatile boolean monitor = true;

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

    //发送text信息
    void postTextInfo(String content) {
        SocketTextEvent event = new SocketTextEvent();
        event.content = content;
        event.type = BaseSocketEvent.TYPE_TEXT;
        EventBus.getDefault().post(event);
    }

    public void setMonitor(boolean monitor) {
        this.mMonitor = monitor;
    }

    //文件最后的状态，成功、失败
    void postFileFinishEvent(int status) {
        if (thread != null) {
            thread.setMonitor(false);
        }
        current_transfer_status = status;
        postReceiverFileInfo();
    }


    //关闭流
    public void closeStreame() {
        try {
            if (input != null) {
                input.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
