package org.hobart.facetrans.model;

import org.hobart.facetrans.FTType;

import java.io.Serializable;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class FTFile implements Serializable {

    /**
     * 编号
     */
    private long id;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private FTType fileType;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件显示名称
     */
    private String name;

    /**
     * 文件大小描述
     */
    private String sizeDesc;

    public FTFile() {

    }

    public FTFile(String filePath, long size) {
        this.filePath = filePath;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FTType getFileType() {
        return fileType;
    }

    public void setFileType(FTType fileType) {
        this.fileType = fileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSizeDesc() {
        return sizeDesc;
    }

    public void setSizeDesc(String sizeDesc) {
        this.sizeDesc = sizeDesc;
    }

    @Override
    public String toString() {
        return "FTFile{" +
                "filePath='" + filePath + '\'' +
                ", fileType=" + fileType +
                ", size=" + size +
                ", name='" + name + '\'' +
                ", sizeDesc='" + sizeDesc + '\'' +
                '}';
    }
}
