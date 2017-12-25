package org.hobart.facetrans.socket.transfer;

import org.hobart.facetrans.model.TransferModel;

/**
 * 数据传输协议
 * <p>
 * 1、第一次握手、发送握手信号
 * 2、第二次握手、接收端收到握手信号之后，进行确认，同时发送确认包给发送端
 * 3、第三次握手、发送端收到接收端发送的确认包之后，再次像接收端发送确认信号，发送完毕之后，接送端和发送端可以正常的进行数据通信了
 * Created by huzeyin on 2017/12/21.
 */

public class TransferProtocol {

    /**
     * 握手信号
     */
    public static byte TYPE_ACK = 0;

    /**
     * ack 确认信号
     */
    public static byte TYPE_CONFIRM_ACK = 1;

    /**
     * 断开连接信号
     */
    public static byte TYPE_DISCONNECT = 2;

    /**
     * 数据传输信号
     */
    public static byte TYPE_DATA_TRANSFER = 3;

    /**
     * 不匹配信号
     */
    public static byte TYPE_MISS_MATCH = 4;

    /**
     * 同步信号 保留字段
     */
    public static byte TYPE_SYNC = 5;
    /**
     * 数据传输完成信号
     */
    public static byte TYPE_TYPE_DATA_TRANSFER_FINISH = 6;
    /**
     * 同步序列编号
     */
    public byte ssm;

    /**
     * 协议类型
     */
    public byte type;

    /**
     * SSID 用来匹配使用的
     */
    public String ssid;

    /**
     * 传输的数据包装
     */
    public TransferModel transferData;

//    @Override
//    public String toString() {
//        return "TransferProtocol{" +
//                "ssm=" + ssm +
//                ", type=" + type +
//                ", ssid='" + ssid + '\'' +
//                ", transferData=" + transferData.toString() +
//                '}';
//    }
}
