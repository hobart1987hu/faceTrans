package org.hobart.facetrans.event;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketStatusEvent {
    //连接成功
    public static final int CONNECTED_SUCCESS = 1;
    //连接失败
    public static final int CONNECTED_FAILED = 2;
    //断开连接
    public static final int DIS_CONNECTED = 3;
    
    public int status;
}
