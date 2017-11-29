package org.hobart.facetrans.socket.transfer.thread;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.event.ZipFTFileEvent;
import org.hobart.facetrans.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class ZipFTFileRunnable implements Runnable {

    private String filePath;
    private String fileName;
    private int index;

    public ZipFTFileRunnable(int index, String filePath, String fileName) {
        this.index = index;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    /**
     *
     */
    @Override
    public void run() {
        //文件存放的路径
        File file = new File(filePath);
        List<File> files = new ArrayList<>();
        files.add(file);
        File zipFile = new File(GlobalConfig.getTransferZipDirectory() + fileName + ".zip");
        if (!zipFile.exists()) {
            new File(zipFile.getParent()).mkdirs();
        }
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileUtils.zipFiles(files, zipFile);
            postEvent(ZipFTFileEvent.ZIP_SUCCESS, zipFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.delete(zipFile);
            postEvent(ZipFTFileEvent.ZIP_FAILURE, "");
        }
    }

    private void postEvent(int status, String filePath) {
        ZipFTFileEvent event = new ZipFTFileEvent();
        event.index = index;
        event.status = status;
        event.zipFilePath = filePath;
        EventBus.getDefault().post(event);
    }
}
