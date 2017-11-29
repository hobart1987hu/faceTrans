package org.hobart.facetrans.socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzeyin on 2017/11/29.
 */

public class SocketExecutorService {

    public static ExecutorService mExecute = Executors.newCachedThreadPool();

    public static ExecutorService getExecute() {
        return mExecute;
    }
}
