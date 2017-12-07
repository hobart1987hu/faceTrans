package org.hobart.facetrans.socket.transfer;

import com.google.gson.Gson;

import org.hobart.facetrans.model.TransferModel;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketTransferQueue {

    private static SocketTransferQueue sInstance = null;

    private LinkedBlockingDeque<TransferModel> mTransferQueue = new LinkedBlockingDeque();

    private static ReentrantLock LOCK = new ReentrantLock();

    public static SocketTransferQueue getInstance() {
        try {

            LOCK.lock();

            if (null == sInstance)
                sInstance = new SocketTransferQueue();

        } finally {
            LOCK.unlock();
        }
        return sInstance;
    }

    private SocketTransferQueue() {

    }

    /**
     * 发送传输文件列表
     */
    public void sendFTFileList(List<TransferModel> transferModels) {
        Gson gson = new Gson();
        String ftFileLists = gson.toJson(transferModels);
        TransferModel ftFileListModel = new TransferModel();
        ftFileListModel.type = TransferModel.TYPE_TRANSFER_DATA_LIST;
        ftFileListModel.content = ftFileLists;
        ftFileListModel.mode = TransferModel.OPERATION_MODE_SEND;
        ftFileListModel.transferStatus = TransferStatus.WAITING;
        put(ftFileListModel);
    }

    /**
     * 发送心跳包
     */
    public void sendHeartMsg() {
        TransferModel heartBeatModel = new TransferModel();
        heartBeatModel.type = TransferModel.TYPE_HEART_BEAT;
        heartBeatModel.content = TransferModel.CONTENT_HEART_BEAT + System.currentTimeMillis();
        heartBeatModel.mode = TransferModel.OPERATION_MODE_SEND;
        put(heartBeatModel);
    }

    /**
     * 发送数据
     *
     * @param model
     */
    public void sendTranferData(TransferModel model) {
        TransferModel fileTransModel = new TransferModel();
        fileTransModel.mode = TransferModel.OPERATION_MODE_SEND;
        fileTransModel.type = model.type;
        fileTransModel.transferStatus = TransferStatus.WAITING;
        fileTransModel.filePath = model.filePath;
        fileTransModel.fileSize = model.fileSize;
        fileTransModel.id = model.id;
        fileTransModel.fileName = model.fileName;
        put(fileTransModel);
    }

    public void put(TransferModel transModel) {

        try {
            mTransferQueue.put(transModel);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TransferModel take() throws InterruptedException {
        return mTransferQueue.take();
    }

    public void clear() {
        mTransferQueue.clear();
    }
}
