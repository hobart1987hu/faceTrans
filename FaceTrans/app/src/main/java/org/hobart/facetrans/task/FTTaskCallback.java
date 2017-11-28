package org.hobart.facetrans.task;

/**
 * Created by huzeyin on 2017/11/20.
 */

public interface FTTaskCallback<Result> {

    void onPreExecute();

    void onCancelled();

    void onFinished(Result result);
}
