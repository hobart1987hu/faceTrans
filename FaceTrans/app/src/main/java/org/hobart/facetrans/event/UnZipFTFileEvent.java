package org.hobart.facetrans.event;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class UnZipFTFileEvent {

    public int status;

    public int index;

    public static final int UNZIP_SUCCESS = 1;

    public static final int UNZIP_FAILURE = 2;
}
