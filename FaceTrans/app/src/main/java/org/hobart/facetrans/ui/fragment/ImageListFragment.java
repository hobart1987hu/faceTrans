package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.view.View;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.ImageAsyncTask;
import org.hobart.facetrans.ui.adapter.FTInfoAdapter;
import org.hobart.facetrans.util.ToastUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class ImageListFragment extends BaseListFragment {


    public static ImageListFragment newInstance(FTType type) {
        ImageListFragment fragment = new ImageListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type.getValue());
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initView(View rootView) {

        gridView.setNumColumns(3);

        new ImageAsyncTask(new FTTaskCallback<List<Image>>() {
            @Override
            public void onPreExecute() {
                showProgressBar();
            }

            @Override
            public void onCancelled() {
                hideProgressBar();
            }

            @Override
            public void onFinished(List<Image> images) {
                hideProgressBar();
                if (images != null && images.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(images);
                    mFTInfoAdapter = new FTInfoAdapter(getContext(), images, FTType.IMAGE);
                    gridView.setAdapter(mFTInfoAdapter);

                } else {
                    ToastUtils.showLongToast("暂时找不到应用的信息");
                }
            }
        }).executeOnExecutor(MAIN_EXECUTOR);
    }
}
