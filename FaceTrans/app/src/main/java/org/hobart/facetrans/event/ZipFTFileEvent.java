package org.hobart.facetrans.event;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class ZipFTFileEvent {

    public int status;

    public String zipFilePath;

    public int index;

    public static final int ZIP_SUCCESS = 1;

    public static final int ZIP_FAILURE = 2;
}
