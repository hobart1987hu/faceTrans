package org.hobart.facetrans.task.impl;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class MusicAsyncTask extends FTTask<List<Music>> {

    public MusicAsyncTask(FTTaskCallback<List<Music>> callback) {
        super(callback);
    }

    @Override
    protected List<Music> execute() {
        List<Music> musics = FileUtils.getSpecificTypeFiles(new String[]{GlobalConfig.EXTEND_MP3});
        musics = FileUtils.getDetailFTFiles(musics, FTType.MUSIC);
        return musics;
    }

}
