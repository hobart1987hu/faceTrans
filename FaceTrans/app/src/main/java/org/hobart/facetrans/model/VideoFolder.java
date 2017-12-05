package org.hobart.facetrans.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by huzeyin on 2017/12/5.
 */

public class VideoFolder {

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
    private List<Video> videos;
    /**
     * 第一条视频的缩略图
     */
    private Bitmap firstVideoBitmap;

    /**
     * 文件夹的路径
     */
    private String folderPath;

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public Bitmap getFirstVideoBitmap() {
        return firstVideoBitmap;
    }

    public void setFirstVideoBitmap(Bitmap firstVideoBitmap) {
        this.firstVideoBitmap = firstVideoBitmap;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
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
        return "VideoFolder{" +
                "folderName='" + folderName + '\'' +
                ", folderCreateDate='" + folderCreateDate + '\'' +
                ", folderFileNum=" + folderFileNum +
                ", videos=" + videos +
                ", firstVideoBitmap=" + firstVideoBitmap +
                '}';
    }
}
