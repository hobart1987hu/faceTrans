package org.hobart.facetrans.task.impl;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.model.Apk;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class ApkAsyncTask extends FTTask<List<Apk>> {

    public ApkAsyncTask(FTTaskCallback callback) {
        super(callback);
    }

    @Override
    protected List<Apk> execute() {
        List<Apk> apks = FileUtils.getSpecificTypeFiles(new String[]{GlobalConfig.EXTEND_APK});
        apks = FileUtils.getDetailFTFiles(apks, FTType.APK);
        return apks;
    }
}
