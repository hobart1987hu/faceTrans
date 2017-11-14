package org.hobart.facetrans.socket;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class SocketConstants {

    //端口号
    public static final int SERVER_PORT = 6666;
    //Socket 连接超时
    public static final int SOCKET_CONNECTED_TIME_OUT = 10 * 1000;
    //Socket 连接超时
    public static final int SOCKETSERVER_CONNECTED_TIME_OUT = 15 * 1000;
    //Socket 缓存区大小
    public static final int TCP_BUFFER_SIZE = 1024 * 1024;
    //Socket 心跳检测超时时间
    public static final int HEART_BEAT_TIME_OUT = 5 * 1000;
    //客户端标识
    public static final int SOCKET_CLIENT_FLAG = 1;
    //服务端标识
    public static final int SOCKET_SERVER_FLAG = 2;
    //断开连接
    public static final String SOCKET_DIS_CONNECTED = "dis";

    //action
    public static final String ACTION_CREATE_SERVER_SOCKET = "org.hobart.create.server_socket";

    public static final String ACTION_STOP_SERVER_SOCKET = "org.hobart.stop.server_socket";

    public static final String ACTION_CREATE_CLIENT_SOCKET = "org.hobart.create.client_socket";

    public static final String ACTION_STOP_CLIENT_SOCKET = "org.hobart.stop.client_socket";
}
