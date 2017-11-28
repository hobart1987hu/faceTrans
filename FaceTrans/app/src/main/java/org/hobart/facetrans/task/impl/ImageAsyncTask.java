package org.hobart.facetrans.task.impl;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class ImageAsyncTask extends FTTask<List<Image>> {

    public ImageAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Image> execute() {
        List<Image> images = FileUtils.getSpecificTypeFiles(new String[]{GlobalConfig.EXTEND_JPG, GlobalConfig.EXTEND_JPEG});
        images = FileUtils.getDetailFTFiles(images, FTType.IMAGE);
        return images;
    }
}
