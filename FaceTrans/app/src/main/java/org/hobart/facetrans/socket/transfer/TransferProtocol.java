package org.hobart.facetrans.socket.transfer;

/**
 * 数据传输协议
 * Created by huzeyin on 2017/12/21.
 */

public class TransferProtocol {

    /**
     * 握手信号
     */
    public static byte TYPE_ACK = 0;

    /**
     * 断开连接信号
     */
    public static byte TYPE_DISCONNECT = 1;

    /**
     * 确认断开连接信号
     */
    public static byte TYPE_ACK_DISCONNECT = 2;

    /**
     * 开始传输数据 信号
     */
    public static byte TYPE_START_TRANSFER = 3;

    /**
     * 不匹配信号
     */
    public static byte TYPE_MISS_MATCH = 4;

    /**
     * 同步信号 保留字段
     */
    public static byte TYPE_SYNC = 5;


    /**
     * 同步序列编号
     */
    private byte ssm;

    /**
     * 协议类型
     */
    private byte type;


    /**
     * SSID 用来匹配使用的
     */
    private String ssid;

    public byte getSsm() {
        return ssm;
    }

    public void setSsm(byte ssm) {
        this.ssm = ssm;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
}
