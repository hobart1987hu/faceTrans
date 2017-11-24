package org.hobart.facetrans.entity;

/**
 * Created by huzeyin on 2017/11/21.
 */

public class HomeInfo {

    /**
     * 音乐列表
     */
    public static final int MUSIC_LIST = 0x110;

    /**
     * 视频列表
     */
    public static final int VIDEO_LIST = 0x111;

    /**
     * 图片集
     */
    public static final int IMAGE_LIST = 0x112;


    /**
     * APP列表
     */
    public static final int APP_LIST = 0x113;

    private String value;
    private int pointer;

    public HomeInfo(String value, int pointer) {
        this.value = value;
        this.pointer = pointer;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
    }
}
