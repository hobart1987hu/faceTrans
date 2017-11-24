package org.hobart.facetrans.socket.transfer.bean;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SocketTextTransferBean extends SocketTransferBean {

    private String content;

    public SocketTextTransferBean(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
