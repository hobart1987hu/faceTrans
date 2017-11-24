package org.hobart.facetrans.event;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketFileEvent extends BaseSocketEvent {


    public String fileName;

    public String fileSavePath;

    public int progress;

    public int status;

    //发送，接收
    public int mode;

    public String id;

}
