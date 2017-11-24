package org.hobart.facetrans.task;

import android.os.AsyncTask;

import org.hobart.facetrans.FTType;

/**
 * Created by huzeyin on 2017/11/20.
 */

public abstract class FTTask<Result> extends AsyncTask<FTType, Void, Result> {

    private final FTTaskCallback<Result> mCallback;
    private boolean isCancelled = false;

    public FTTask(FTTaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallback.onPreExecute();
    }

    @Override
    protected Result doInBackground(FTType... params) {
        if (isCancelled()) {
            isCancelled = true;
            mCallback.onCancelled();
            return null;
        }
        return execute();
    }

    protected abstract Result execute();

    @Override
    protected void onPostExecute(Result r) {
        super.onPostExecute(r);
        if (isCancelled()) {
            if (!isCancelled)
                mCallback.onCancelled();
        } else {
            mCallback.onFinished(r);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        isCancelled = true;
    }
}

