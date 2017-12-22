package org.hobart.facetrans.event;

import org.hobart.facetrans.socket.transfer.TransferProtocol;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketTransferEvent extends TransferProtocol {

    public int connectStatus = SOCKET_CONNECT_SUCCESS;

    public static final int SOCKET_CONNECT_SUCCESS = 1;

    public static final int SOCKET_CONNECT_FAILURE = 2;
}
