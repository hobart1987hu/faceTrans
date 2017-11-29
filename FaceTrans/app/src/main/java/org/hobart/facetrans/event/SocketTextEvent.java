package org.hobart.facetrans.event;

/**
 * event bus发送的 socket 文本传输
 * Created by huzeyin on 2017/11/24.
 */

public class SocketTextEvent extends SocketEvent {

    public String content;

    public SocketTextEvent(int type, int status, int mode) {
        super(type, status, mode);
    }
}
