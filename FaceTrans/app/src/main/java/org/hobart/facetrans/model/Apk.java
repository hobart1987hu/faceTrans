package org.hobart.facetrans.model;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class Apk extends FTFile {

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    /**
     * 当前版本号
     */
    private String versionName;

}
