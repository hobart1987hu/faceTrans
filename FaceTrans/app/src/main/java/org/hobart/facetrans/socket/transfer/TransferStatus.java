package org.hobart.facetrans.socket.transfer;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class TransferStatus {
    //未知
    public static final int UN_KNOW = -1;
    //等待中
    public static final int WAITING = 1;
    //传送中
    public static final int TRANSFERING = 2;
    //传输成功
    public static final int TRANSFER_SUCCESS = 3;
    //传输失败
    public static final int TRANSFER_FAILED = 4;
    //压缩中
    public static final int ZIP = 5;
    //解压中
    public static final int UNZIP = 6;
    //失败
    public static final int FAILED = 8;
    //完成
    public static final int FINISH = 9;

}
