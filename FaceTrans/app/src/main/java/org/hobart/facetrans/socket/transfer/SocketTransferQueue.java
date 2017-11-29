package org.hobart.facetrans.socket.transfer;

import com.google.gson.Gson;

import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.transfer.model.FileTransModel;
import org.hobart.facetrans.socket.transfer.model.TextTransModel;
import org.hobart.facetrans.socket.transfer.model.TransModel;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketTransferQueue {

    private static SocketTransferQueue sInstance = null;

    private LinkedBlockingDeque<TransModel> mTransferQueue = new LinkedBlockingDeque();

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
        TextTransModel ftFileListModel = new TextTransModel(TransModel.TYPE_LIST, ftFileLists);
        ftFileListModel.mode = TransModel.OPERATION_MODE_SEND;
        put(ftFileListModel);
    }

    /**
     * 发送心跳包
     */
    public void sendHeartMsg() {
//        TextTransModel heartBeatModel = new TextTransModel(TransModel.TYPE_HEART_BEAT, TransModel.CONTENT_HEART_BEAT);
//        heartBeatModel.mode = TransModel.OPERATION_MODE_SEND;
//        put(heartBeatModel);
    }

    public void sendSingleFTFile(TransferModel transferModel, String filePath, boolean isZipFile) {
        FileTransModel fileTransModel = new FileTransModel();
        fileTransModel.mode = FileTransModel.OPERATION_MODE_SEND;
        fileTransModel.type = FileTransModel.TYPE_FILE;
        fileTransModel.status = TransferStatus.TRANSFERING;
        fileTransModel.setZipFile(isZipFile);
        fileTransModel.setFilePath(filePath);
        fileTransModel.setFileSize(Long.parseLong(transferModel.getSize()));
        fileTransModel.setId(transferModel.getId());
        fileTransModel.setFileName(transferModel.getFileName());
        put(fileTransModel);
    }


    public void put(TransModel transModel) {

        try {
            mTransferQueue.put(transModel);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TransModel take() throws InterruptedException {
        return mTransferQueue.take();
    }

    public void clear() {
        mTransferQueue.clear();
    }
}
