package org.hobart.facetrans.manager;

import android.content.Context;
import android.text.TextUtils;

import org.hobart.facetrans.model.FTFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class FTFileManager {

    private static FTFileManager sInstance = null;

    private static final ReentrantLock LOCK = new ReentrantLock();

    private Map<Long, FTFile> mFTFiles = new HashMap<>();

    private AtomicLong mAtomicId = new AtomicLong(2);

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

    public Map<Long, FTFile> getFTFiles() {
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
        return mFTFiles.containsKey(ftFile.getId());
    }

    public void delFTFile(FTFile ftFile) {
        if (mFTFiles.containsKey(ftFile.getId())) {
            mFTFiles.remove(ftFile.getId());
        }
    }

    public void addFTFile(FTFile ftFile) {
        if (!mFTFiles.containsKey(ftFile.getId())) {
            ftFile.setId(mAtomicId.incrementAndGet());
            mFTFiles.put(ftFile.getId(), ftFile);
        }
    }

    public FTFile getFTFile(long key) {

        if (key <= 0) return null;

        return mFTFiles.get(key);
    }

    public void clear() {
        mFTFiles.clear();
    }
}
