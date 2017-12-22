package org.hobart.facetrans;

import android.os.Environment;

import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.util.AndroidUtils;

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
    public static final String EXTEND_MP3 = ".mp3";
    public static final String EXTEND_MP4 = ".mp4";

    /**
     * Wifi连接上时 未分配默认的Ip地址
     */
    public static final String DEFAULT_UNKOWN_IP = "0.0.0.0";

    /**
     * 最大尝试数
     */
    public static final int DEFAULT_TRY_TIME = 1 * 100;

    /**
     * asset 资源名称
     */
    public static final String NAME_FILE_TEMPLATE = "file.template";
    public static final String NAME_CLASSIFY_TEMPLATE = "classify.template";

    public static final String WEB_TRANSFER_DOWNLOAD_PREFIX = "http://192.168.43.1:1234/download/";
    public static final String WEB_TRANSFER_IMAGE_PREFIX = "http://192.168.43.1:1234/image/";
    public static final String WEB_TRANSFER_APP_ICON_IMAGE_PREFIX = WEB_TRANSFER_IMAGE_PREFIX + "appIcon/";
    public static final String WEB_TRANSFER_APK_IMAGE_PREFIX = WEB_TRANSFER_IMAGE_PREFIX + "apk/";
    public static final String WEB_TRANSFER_MUSIC_IMAGE_PREFIX = WEB_TRANSFER_IMAGE_PREFIX + "music/";
    public static final String WEB_TRANSFER_VIDEO_IMAGE_PREFIX = WEB_TRANSFER_IMAGE_PREFIX + "video/";

    /**
     * 网页传标识
     */
    public static final String KEY_WEB_TRANSFER_FLAG = "KEY_WEB_TRANSFER_FLAG";


    public static String getTransferDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "facetrans";
    }

    public static final String getApkIconDirectory() {
        return getTransferDirectory() + File.separator + "apkIcons" + File.separator;
    }

    public static final String getMusicIconDirectory() {
        return getTransferDirectory() + File.separator + "musicIcons" + File.separator;
    }

    public static final String getVideoIconDirectory() {
        return getTransferDirectory() + File.separator + "videoIcons" + File.separator;
    }

    public static final boolean DEBUG = true;

    /**
     * AP 密码
     */
    public static final String AP_PWD = "1234567890";

    public static final String AP_SSID_PREFIX = "FT_HOTSPOT_";

    /**
     * AP SSID
     */
    public static final String AP_SSID = AP_SSID_PREFIX + AndroidUtils.getPhoneModel();

    public static final String WEB_SERVER_IP = "192.168.43.1";


    public static final Comparator<Map.Entry<Long, FTFile>> DEFAULT_COMPARATOR = new Comparator<Map.Entry<Long, FTFile>>() {
        public int compare(Map.Entry<Long, FTFile> o1, Map.Entry<Long, FTFile> o2) {
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
