package org.hobart.facetrans;

import android.os.Environment;

import org.hobart.facetrans.model.FTFile;

import java.io.File;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class GlobalConfig {
    /**
     * 常见文件拓展名
     */
    public static final String EXTEND_APK = ".apk";
    public static final String EXTEND_JPEG = ".jpeg";
    public static final String EXTEND_JPG = ".jpg";
    public static final String EXTEND_PNG = ".png";
    public static final String EXTEND_MP3 = ".mp3";
    public static final String EXTEND_MP4 = ".mp4";


    public static String getTransferDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "facetrans";
    }

    public static final boolean DEBUG = true;

    /**
     * 默认的Wifi SSID
     */
    public static final String DEFAULT_SSID = "FT_HOTSPOT";


    public static final Comparator<Map.Entry<String, FTFile>> DEFAULT_COMPARATOR = new Comparator<Map.Entry<String, FTFile>>() {
        public int compare(Map.Entry<String, FTFile> o1, Map.Entry<String, FTFile> o2) {
            if (o1.getValue().getFileType().getValue() > o2.getValue().getFileType().getValue()) {
                return 1;
            } else if (o1.getValue().getFileType().getValue() < o2.getValue().getFileType().getValue()) {
                return -1;
            } else {
                return 0;
            }
        }
    };
}
