package org.hobart.facetrans.model;

/**
 * Created by huzeyin on 2017/11/28.
 */

public class TransferModel {
    /**
     * 文件编号
     */
    private String id;
    /**
     * 传输进度
     */
    private int progress;
    /**
     * 传输状态 （等待-传输中-完成-失败）
     */
    private int transferStatus;
    /**
     * 问否已经传输
     */
    private boolean selectedTransfer;
    /**
     * 文件大小
     */
    private String size;
    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件图标
     */
    private String fileIcon;

    /**
     * 文件的路径
     */
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }

    public boolean isSelectedTransfer() {
        return selectedTransfer;
    }

    public void setSelectedTransfer(boolean selectedTransfer) {
        this.selectedTransfer = selectedTransfer;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
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
                '}';
    }
}
