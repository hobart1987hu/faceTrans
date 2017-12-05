package org.hobart.facetrans.task.impl;

import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.task.FTTask;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.util.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class ImageAsyncTask extends FTTask<List<ImageFolder>> {

    public ImageAsyncTask(FTTaskCallback<List<ImageFolder>> callback) {
        super(callback);
    }

    @Override
    protected List<ImageFolder> execute() {

        List<ImageFolder> folders = FileUtils.loadLocalFolderContainsImage();

        if (null != folders && folders.size() > 0) {
            for (ImageFolder folder : folders) {
                folder.setImages(FileUtils.queryFolderPictures(new File(folder.getFirstFilePath()).getParentFile().getAbsolutePath()));
            }
        }
        return folders;
    }
}
