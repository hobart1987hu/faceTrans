package org.hobart.facetrans.event;

/**
 * Created by huzeyin on 2017/12/19.
 */

public class ScanWifiEvent {

    public static final int SCAN_SUCCESS = 1;

    public static final int SCAN_FAILED = 2;

    public String ssid;

    public int status;

}
