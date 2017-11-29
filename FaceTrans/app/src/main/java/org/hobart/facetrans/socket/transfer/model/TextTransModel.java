package org.hobart.facetrans.socket.transfer.model;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class TextTransModel extends TransModel {

    private String content;

    public TextTransModel(int type, String content) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TextTransModel{" +
                "content='" + content + '\'' +
                '}';
    }
}
