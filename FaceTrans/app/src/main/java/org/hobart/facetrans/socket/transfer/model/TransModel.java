package org.hobart.facetrans.socket.transfer.model;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class TransModel {
    /**
     * 发送心跳
     */
    public static final int TYPE_HEART_BEAT = 1;

    /**
     * 发送列表数据
     */
    public static final int TYPE_LIST = 2;

    /**
     * 发送文件
     */
    public static final int TYPE_FILE = 3;

    /**
     * 传输类型
     */
    public int type;

    // 接收操作
    public static final int OPERATION_MODE_SEND = 0;
    // 发送操作
    public static final int OPERATION_MODE_RECEIVER = 1;

    //心跳包文本内容
    public static final String CONTENT_HEART_BEAT = "$HB$";

    /**
     * {@link org.hobart.facetrans.socket.transfer.TransferStatus}
     */
    public int status;

    public int mode;

}
