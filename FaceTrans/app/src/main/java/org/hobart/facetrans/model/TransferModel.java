package org.hobart.facetrans.model;

import android.graphics.drawable.Drawable;

import org.hobart.facetrans.FTType;

/**
 * Created by huzeyin on 2017/11/28.
 */

public class TransferModel {

    // 接收操作
    public static final int OPERATION_MODE_SEND = 0;
    // 发送操作
    public static final int OPERATION_MODE_RECEIVER = 1;

    public static final String CONTENT_HEART_BEAT = "$HB";

    /**
     * 发送心跳
     */
    public static final int TYPE_HEART_BEAT = 1;

    /**
     * 发送列表数据
     */
    public static final int TYPE_TRANSFER_DATA_LIST = 2;

    /**
     * 发送文件
     */
    public static final int TYPE_FILE = 3;

    /**
     * 发送音乐
     */
    public static final int TYPE_MUSIC = 4;

    /**
     * 发送图片
     */
    public static final int TYPE_IMAGE = 5;

    /**
     * 发送apk包
     */
    public static final int TYPE_APK = 6;

    /**
     * 发送视频文件
     */
    public static final int TYPE_VIDEO = 7;

    /**
     * 发送文件夹包
     */
    public static final int TYPE_FOLDER = 8;

    /**
     * 文件编号
     */
    public String id;
    /**
     * 传输进度
     */
    public int progress;
    /**
     * 传输状态 {@link org.hobart.facetrans.socket.transfer.TransferStatus }
     */
    public int transferStatus;
    /**
     * 问否已经传输
     */
    public boolean selectedTransfer;
    /**
     * 文件大小
     */
    public String size;

    /**
     * 文件大小
     */
    public long fileSize;

    /**
     * 文件名称
     */
    public String fileName;

    /**
     * 文件图标
     */
    public String fileIcon;

    /**
     * 文件的路径
     */
    public String filePath;

    /**
     * 传输模式，发送还是接收
     */
    public int mode;


    /**
     * 传输的数据类型
     */
    public int type;

    /**
     * 传输的文本内容
     */
    public String content;
    /**
     * 文件保存路径
     */
    public String savePath;

    @Override
    public TransferModel clone() {
        TransferModel temp = new TransferModel();
        temp.fileIcon = this.fileIcon;
        temp.type = this.type;
        temp.content = this.content;
        temp.mode = this.mode;
        temp.filePath = this.filePath;
        temp.fileName = this.fileName;
        temp.fileSize = this.fileSize;
        temp.id = this.id;
        temp.progress = this.progress;
        temp.transferStatus = this.transferStatus;
        temp.size = this.size;
        temp.selectedTransfer = this.selectedTransfer;
        temp.savePath = this.savePath;
        return temp;
    }

    @Override
    public String toString() {
        return "TransferModel{" +
                "id='" + id + '\'' +
                ", progress=" + progress +
                ", transferStatus=" + transferStatus +
                ", selectedTransfer=" + selectedTransfer +
                ", size='" + size + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileIcon='" + fileIcon + '\'' +
                ", filePath='" + filePath + '\'' +
                ", mode=" + mode +
                ", type=" + type +
                ", content='" + content + '\'' +
                '}';
    }

    public static int convertFileType(FTType type) {
        if (type.getValue() == 1) {
            return TYPE_MUSIC;
        } else if (type.getValue() == 2) {
            return TYPE_VIDEO;
        } else if (type.getValue() == 3) {
            return TYPE_APK;
        } else if (type.getValue() == 4) {
            return TYPE_FILE;
        } else if (type.getValue() == 5) {
            return TYPE_IMAGE;
        }
        return 0;
    }
}
