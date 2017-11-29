package org.hobart.facetrans.socket.transfer.thread;

import org.hobart.facetrans.util.FileUtils;

import java.io.File;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class DeleteTransFileRunnable implements Runnable {

    private String deleteFilePath;

    public DeleteTransFileRunnable(String deleteFilePath) {
        this.deleteFilePath = deleteFilePath;
    }

    @Override
    public void run() {
        File deleteFile = new File(deleteFilePath);
        if (deleteFile.exists()) {
            FileUtils.delete(deleteFile);
        }
    }
}
