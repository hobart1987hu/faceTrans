package org.hobart.facetrans.manager;

import org.hobart.facetrans.model.FTFile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class FTFileManager {

    private static FTFileManager sInstance = null;

    private static final ReentrantLock LOCK = new ReentrantLock();

    private Map<String, FTFile> mFTFiles = new HashMap<>();


    public static FTFileManager getInstance() {
        try {
            LOCK.lock();
            if (null == sInstance)
                sInstance = new FTFileManager();
        } finally {
            LOCK.unlock();
        }
        return sInstance;
    }


    private FTFileManager() {

    }

    public Map<String, FTFile> getFTFiles() {
        return mFTFiles;
    }

    public boolean isFTFilesExist() {
        if (mFTFiles == null || mFTFiles.size() <= 0) {
            return false;
        }
        return true;
    }

    public boolean isFTFileExist(FTFile ftFile) {
        if (mFTFiles == null) return false;
        return mFTFiles.containsKey(ftFile.getFilePath());
    }

    public void delFTFile(FTFile ftFile) {
        if (mFTFiles.containsKey(ftFile.getFilePath())) {
            mFTFiles.remove(ftFile.getFilePath());
        }
    }

    public void addFTFile(FTFile ftFile) {
        if (!mFTFiles.containsKey(ftFile.getFilePath())) {
            mFTFiles.put(ftFile.getFilePath(), ftFile);
        }
    }

    public void clear() {
        mFTFiles.clear();
    }
}
