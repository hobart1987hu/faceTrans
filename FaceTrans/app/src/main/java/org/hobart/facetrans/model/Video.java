package org.hobart.facetrans.model;

import org.hobart.facetrans.util.DateUtils;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class Video extends FTFile {

    /**
     * 视频的总时长
     */
    private long duration;

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 添加的日期时间
     */
    private long dateAdded;

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    private String videoAddTime;

    public String getVideoAddTime() {
        return DateUtils.timestamp2Date("" + dateAdded);
    }

    @Override
    public String toString() {
        String temp = super.toString();
        temp = temp + " 总时长：" + duration + " 视频创建时间：" + getVideoAddTime();
        return temp;
    }
}
