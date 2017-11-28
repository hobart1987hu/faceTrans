package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.view.View;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.VideoAsyncTask;
import org.hobart.facetrans.ui.adapter.FTInfoAdapter;
import org.hobart.facetrans.util.ToastUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class VideoListFragment extends BaseListFragment {

    public static VideoListFragment newInstance(FTType type) {
        VideoListFragment fragment = new VideoListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type.getValue());
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initView(View rootView) {

        gridView.setNumColumns(1);

        new VideoAsyncTask(new FTTaskCallback<List<Video>>() {
            @Override
            public void onPreExecute() {
                showProgressBar();
            }

            @Override
            public void onCancelled() {
                hideProgressBar();
            }

            @Override
            public void onFinished(List<Video> videos) {
                hideProgressBar();
                if (videos != null && videos.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(videos);
                    mFTInfoAdapter = new FTInfoAdapter(getContext(), videos, FTType.VIDEO);
                    gridView.setAdapter(mFTInfoAdapter);

                } else {
                    ToastUtils.showLongToast("暂时找不到应用的信息");
                }
            }
        }).executeOnExecutor(MAIN_EXECUTOR);
    }
}
