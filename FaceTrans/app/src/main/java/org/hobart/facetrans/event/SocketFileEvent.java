package org.hobart.facetrans.event;

/**
 * event bus发送的 socket 文件传输
 * Created by huzeyin on 2017/11/24.
 */

public class SocketFileEvent extends SocketEvent {

    /**
     * 文件的名字
     */
    public String fileName;
    /**
     * 文件保存路径 如果是发送：这个路径就是压缩文件的路径，如果是接收，这个路径就是文件被接收后，保存的路径
     */
    public String fileSavePath;

    /**
     * 发送进度
     */
    public int progress;

    /**
     * 编号
     */
    public String id;

    /**
     * 是否压缩文件
     */
    public boolean  isZipFile;

    public SocketFileEvent(int type, int status, int mode) {
        super(type, status, mode);
    }
}
