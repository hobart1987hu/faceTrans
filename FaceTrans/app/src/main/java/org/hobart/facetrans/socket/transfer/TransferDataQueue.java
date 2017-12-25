package org.hobart.facetrans.socket.transfer;

import com.google.gson.Gson;

import org.hobart.facetrans.model.TransferModel;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class TransferDataQueue {

    private static TransferDataQueue sInstance = null;

    private LinkedBlockingDeque<TransferProtocol> mTransferQueue = new LinkedBlockingDeque();

    private static ReentrantLock LOCK = new ReentrantLock();

    public static TransferDataQueue getInstance() {
        try {

            LOCK.lock();

            if (null == sInstance)
                sInstance = new TransferDataQueue();

        } finally {
            LOCK.unlock();
        }
        return sInstance;
    }

    private TransferDataQueue() {

    }

    /**
     * 发送ack握手信号
     */
    public void sendAckSignal(String ssid) {

        TransferProtocol transferProtocol = new TransferProtocol();

        transferProtocol.ssid = ssid;

        transferProtocol.type = TransferProtocol.TYPE_ACK;

        transferProtocol.ssm = (byte) (Math.random() * 100);

        put(transferProtocol);

    }

    /**
     * 发送ack确认信号
     *
     * @param transferProtocol
     */
    public void sendAckConfirmSignal(TransferProtocol transferProtocol) {

        transferProtocol.ssm = (byte) (transferProtocol.ssm + 1);

        transferProtocol.type = TransferProtocol.TYPE_CONFIRM_ACK;

        put(transferProtocol);
    }

    /**
     * 发送断开连接信号
     */
    public void sendDisconnect() {

        TransferProtocol transferProtocol = new TransferProtocol();

        transferProtocol.ssm = (byte) (Math.random() * 100);

        transferProtocol.type = TransferProtocol.TYPE_DISCONNECT;

        put(transferProtocol);

    }

    /**
     * 发送不匹配信号
     */
    public void sendMissMatch() {

        TransferProtocol transferProtocol = new TransferProtocol();

        transferProtocol.type = TransferProtocol.TYPE_MISS_MATCH;

        put(transferProtocol);

    }

    /**
     * 发送传输文件列表
     */
    public void sendFTFileList(final List<TransferModel> transferModels) {

        TransferProtocol transferProtocol = new TransferProtocol();

        transferProtocol.type = TransferProtocol.TYPE_DATA_TRANSFER;

        String ftFileLists = new Gson().toJson(transferModels);

        TransferModel ftFileListModel = new TransferModel();
        ftFileListModel.type = TransferModel.TYPE_TRANSFER_DATA_LIST;
        ftFileListModel.content = ftFileLists;
        ftFileListModel.mode = TransferModel.OPERATION_MODE_SEND;
        ftFileListModel.transferStatus = TransferStatus.WAITING;

        transferProtocol.transferData = ftFileListModel;

        put(transferProtocol);
    }

    /**
     * 发送数据
     *
     * @param model
     */
    public void sendTransferData(TransferModel model) {

        TransferProtocol transferProtocol = new TransferProtocol();

        transferProtocol.type = TransferProtocol.TYPE_DATA_TRANSFER;

        TransferModel transferModel = new TransferModel();
        transferModel.mode = TransferModel.OPERATION_MODE_SEND;
        transferModel.type = model.type;
        transferModel.transferStatus = TransferStatus.WAITING;
        transferModel.filePath = model.filePath;
        transferModel.fileSize = model.fileSize;
        transferModel.id = model.id;
        transferModel.fileName = model.fileName;

        transferProtocol.transferData = transferModel;

        put(transferProtocol);
    }

    /**
     * 发送数据完成
     */
    public void sendTransferDataFinish() {

        TransferProtocol transferProtocol = new TransferProtocol();

        transferProtocol.type = TransferProtocol.TYPE_TYPE_DATA_TRANSFER_FINISH;

        put(transferProtocol);
    }


    public void put(TransferProtocol protocol) {
        try {
            mTransferQueue.put(protocol);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public TransferProtocol take() throws InterruptedException {
        return mTransferQueue.take();
    }

    public void clear() {
        mTransferQueue.clear();
    }
}
