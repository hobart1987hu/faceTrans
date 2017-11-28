package org.hobart.facetrans.model;

import android.graphics.Bitmap;

import org.hobart.facetrans.FTType;

import java.io.Serializable;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class FTFile implements Serializable {

    //必要属性
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

    /**
     * 文件缩略图 (mp4与apk可能需要)
     */
    private Bitmap bitmap;


    public FTFile() {

    }

    public FTFile(String filePath, long size) {
        this.filePath = filePath;
        this.size = size;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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

}
