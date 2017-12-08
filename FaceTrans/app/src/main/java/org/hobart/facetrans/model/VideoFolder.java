package org.hobart.facetrans.model;

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
     * 文件夹中文件的个数
     */
    private int folderFileNum;

    /**
     * 当前文件夹下 图片列表
     */
    private List<Video> videos;

    /**
     * 文件夹icon 存放路径
     */
    private String folderIconPath;

    /**
     * 文件夹的路径
     */
    private String folderPath;

    public String getFolderIconPath() {
        return folderIconPath;
    }

    public void setFolderIconPath(String folderIconPath) {
        this.folderIconPath = folderIconPath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
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
                ", folderFileNum=" + folderFileNum +
                ", videos=" + videos +
                ", folderIconPath='" + folderIconPath + '\'' +
                ", folderPath='" + folderPath + '\'' +
                '}';
    }
}
