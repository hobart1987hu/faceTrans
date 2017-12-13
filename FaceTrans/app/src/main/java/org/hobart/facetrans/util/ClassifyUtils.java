package org.hobart.facetrans.util;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.model.FTFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huzeyin on 2017/12/10.
 */

public class ClassifyUtils {

    public static List<FTFile> filter(Map<Long, FTFile> hashMap, FTType type) {
        List<FTFile> fileInfos = new ArrayList<FTFile>();

        for (Map.Entry<Long, FTFile> entry : hashMap.entrySet()) {
            FTFile fileInfo = entry.getValue();
            if (type == FTType.IMAGE) {
                if (FileUtils.isImageFile(fileInfo.getFilePath())) {
                    fileInfos.add(fileInfo);
                }
            } else if (type == FTType.APK) {
                if (FileUtils.isApkFile(fileInfo.getFilePath())) {
                    fileInfos.add(fileInfo);
                }
            } else if (type == FTType.MUSIC) {
                if (FileUtils.isMusicFile(fileInfo.getFilePath())) {
                    fileInfos.add(fileInfo);
                }
            } else if (type == FTType.VIDEO) {
                if (FileUtils.isVideoFile(fileInfo.getFilePath())) {
                    fileInfos.add(fileInfo);
                }
            }
        }
        return fileInfos;
    }

}
