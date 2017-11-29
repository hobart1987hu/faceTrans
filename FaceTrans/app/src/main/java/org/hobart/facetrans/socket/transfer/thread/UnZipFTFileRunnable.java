package org.hobart.facetrans.socket.transfer.thread;

import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.event.UnZipFTFileEvent;
import org.hobart.facetrans.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class UnZipFTFileRunnable implements Runnable {

    private String filePath;

    private int index;

    public UnZipFTFileRunnable(int index, String filePath) {
        this.index = index;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        try {
            FileUtils.upZipFile(new File(filePath), GlobalConfig.getTransferUnZipDirectory());
            postEvent(UnZipFTFileEvent.UNZIP_SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
            postEvent(UnZipFTFileEvent.UNZIP_FAILURE);
        }
    }

    private void postEvent(int status) {
        UnZipFTFileEvent event = new UnZipFTFileEvent();
        event.index = index;
        event.status = status;
    }
}
