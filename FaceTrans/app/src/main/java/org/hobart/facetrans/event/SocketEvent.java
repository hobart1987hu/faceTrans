package org.hobart.facetrans.event;

import org.hobart.facetrans.model.TransferModel;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketEvent extends TransferModel {

    public SocketEvent(int type, int status, int mode) {
        this.type = type;
        this.transferStatus = status;
        this.mode = mode;
    }
}
