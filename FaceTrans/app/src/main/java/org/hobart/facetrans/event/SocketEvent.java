package org.hobart.facetrans.event;

import org.hobart.facetrans.socket.transfer.model.TransModel;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketEvent extends TransModel {

    public SocketEvent(int type, int status, int mode) {
        this.type = type;
        this.status = status;
        this.mode = mode;
    }
}
