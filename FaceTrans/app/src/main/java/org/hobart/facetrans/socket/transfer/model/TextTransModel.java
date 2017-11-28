package org.hobart.facetrans.socket.transfer.model;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class TextTransModel implements TransModel {
    
    private String content;

    public TextTransModel(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
