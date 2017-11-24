package org.hobart.facetrans.socket.transfer;

import org.hobart.facetrans.event.BaseSocketEvent;
import org.hobart.facetrans.socket.transfer.bean.SocketFileTransferBean;
import org.hobart.facetrans.socket.transfer.bean.SocketTextTransferBean;
import org.hobart.facetrans.socket.transfer.bean.SocketTransferBean;
import org.hobart.facetrans.util.AndroidUtils;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketTransferQueue {

    private static SocketTransferQueue sInstance = null;

    private LinkedBlockingDeque<SocketTransferBean> mTransferQueue = new LinkedBlockingDeque();


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
     * 发送手机设备
     */
    public void sendDeviceModel() {
        String content = BaseSocketEvent.SOCKET_DEVICE_MODEL_HEAD + AndroidUtils.getDeviceModel();
        SocketTextTransferBean bean = new SocketTextTransferBean(content);
        put(bean);
    }

    /**
     * 发送文件
     */
    public void sendFile(SocketFileTransferBean bean) {
        if (bean != null) {
            put(bean);
        }
    }

    /**
     * 文件文件压缩中
     */
    public void sendFileZip() {
        SocketTextTransferBean bean = new SocketTextTransferBean(BaseSocketEvent.SOCKET_SEND_ZIP);
        put(bean);
    }

    /**
     * 所有文件发送完成
     */
    public void sendFileFinish() {
        SocketTextTransferBean bean = new SocketTextTransferBean(BaseSocketEvent.SOCKET_SERVICE_SEND_FINISH);
        put(bean);
    }

    /**
     * 发送心跳检测数据
     */
    public void sendHeartMsg() {
        SocketTextTransferBean hearBeatBean = new SocketTextTransferBean(BaseSocketEvent.SOCKET_HEART_BEAT);
        put(hearBeatBean);
    }

    public void put(SocketTransferBean bean) {
        try {
            mTransferQueue.put(bean);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public SocketTransferBean poll() {
        return mTransferQueue.poll();
    }

    public void clear() {
        mTransferQueue.clear();
    }
}
