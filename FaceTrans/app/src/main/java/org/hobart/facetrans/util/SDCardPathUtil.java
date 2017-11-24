package org.hobart.facetrans.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class SDCardPathUtil {

    /**
     * 面传目录
     *
     * @return
     */
    public static String getTransferDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "facetrans";
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }


    public static boolean isFolderExist(String directoryPath) {
        if (TextUtils.isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return delete(file);
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                delete(f);
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return delete(file);
    }

    public static boolean delete(File file) {
        if (file.isFile()) {
            File f = new File(file.getParent() + "/" + System.currentTimeMillis());
            file.renameTo(f);
            try {
                return f.delete();
//                FileUtils.deleteQuietly(f);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                try {
                    File f = new File(file.getParent() + File.separator + System.currentTimeMillis());
                    file.renameTo(f);
                    return f.delete();
//                    FileUtils.deleteQuietly(f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            File f = new File(file.getParent() + File.separator + System.currentTimeMillis());
            file.renameTo(f);
            try {
                return f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

}
