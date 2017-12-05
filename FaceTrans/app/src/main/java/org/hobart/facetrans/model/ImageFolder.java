package org.hobart.facetrans.model;

import java.util.List;

/**
 * 图片文件夹
 * Created by huzeyin on 2017/11/30.
 */

public class ImageFolder {

    /**
     * 文件夹的名称
     */
    private String folderName;

    /**
     * 文件夹创建日期
     */
    private String folderCreateDate;

    /**
     * 文件夹中文件的个数
     */
    private int folderFileNum;

    /**
     * 当前文件夹下 图片列表
     */
    private List<Image> images;

    /**
     * 文件夹下第一张图片路径
     */
    public String firstFilePath;

    public String getFirstFilePath() {
        return firstFilePath;
    }

    public void setFirstFilePath(String firstFilePath) {
        this.firstFilePath = firstFilePath;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderCreateDate() {
        return folderCreateDate;
    }

    public void setFolderCreateDate(String folderCreateDate) {
        this.folderCreateDate = folderCreateDate;
    }

    public int getFolderFileNum() {
        return folderFileNum;
    }

    public void setFolderFileNum(int folderFileNum) {
        this.folderFileNum = folderFileNum;
    }

    @Override
    public String toString() {
        return "ImageFolder{" +
                "folderName='" + folderName + '\'' +
                ", folderCreateDate='" + folderCreateDate + '\'' +
                ", folderFileNum=" + folderFileNum +
                ", images=" + images +
                ", firstFilePath='" + firstFilePath + '\'' +
                '}';
    }
}
