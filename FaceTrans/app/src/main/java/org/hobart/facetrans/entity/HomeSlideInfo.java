package org.hobart.facetrans.entity;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class HomeSlideInfo {


    /**
     * 二维码扫描
     */
    public static final int QR_SCANNING = 0x11;

    /**
     * 创建Wi-Fi热点
     */
    public static final int CREATE_WIFI_HOT = 0x12;


    String value;
    int pointer;

    public HomeSlideInfo(String value, int pointer) {
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
