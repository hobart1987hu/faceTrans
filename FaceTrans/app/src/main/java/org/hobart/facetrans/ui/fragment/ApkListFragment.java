package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.model.Apk;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.ApkAsyncTask;
import org.hobart.facetrans.ui.adapter.FTInfoAdapter;
import org.hobart.facetrans.util.ToastUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class ApkListFragment extends BaseListFragment {


    public static ApkListFragment newInstance(FTType type) {
        ApkListFragment fragment = new ApkListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type.getValue());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initView(View rootView) {

        gridView.setNumColumns(4);


        new ApkAsyncTask(new FTTaskCallback<List<Apk>>() {
            @Override
            public void onPreExecute() {
                showProgressBar();
            }

            @Override
            public void onCancelled() {
                hideProgressBar();
            }

            @Override
            public void onFinished(List<Apk> apks) {
                hideProgressBar();
                if (apks != null && apks.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(apks);
                    mFTInfoAdapter = new FTInfoAdapter(getContext(), apks, FTType.APK);
                    gridView.setAdapter(mFTInfoAdapter);

                } else {
                    ToastUtils.showLongToast("暂时找不到应用的信息");
                }
            }
        }).executeOnExecutor(MAIN_EXECUTOR);
    }
}
