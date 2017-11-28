package org.hobart.facetrans.task.impl;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class VideoAsyncTask extends FTTask<List<Video>> {

    public VideoAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Video> execute() {
        List<Video> videos = FileUtils.getSpecificTypeFiles(new String[]{GlobalConfig.EXTEND_MP4});
        videos = FileUtils.getDetailFTFiles(videos, FTType.VIDEO);
        return videos;
    }

}
