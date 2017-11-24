package org.hobart.facetrans.model;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class Video extends FTModel {

    /**
     * 视频名称
     */
    private String videoName;

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoName='" + videoName + '\'' +
                '}';
    }
}
