package org.hobart.facetrans.event;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class BaseSocketEvent {
    //发送心跳信息
    public static final int TYPE_TEXT = 0;
    //发送文件信息
    public static final int TYPE_FILE = 1;

    public int type;

    //Socket 接收操作
    public static final int OPERATION_MODE_SEND = 0;
    //Socket 发送操作
    public static final int OPERATION_MODE_RECEIVER = 1;

    //心跳包信息
    public static final String SOCKET_HEART_BEAT = "$HB$";
    //设备型号头部
    public static final String SOCKET_DEVICE_MODEL_HEAD = "$DMH$";
    //文件压缩中
    public static final String SOCKET_SEND_ZIP = "$SSZ$";
    //发送文件完成
    public static final String SOCKET_SERVICE_SEND_FINISH = "$SSSF$";

}
