package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.view.View;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.MusicAsyncTask;
import org.hobart.facetrans.ui.adapter.FTInfoAdapter;
import org.hobart.facetrans.util.ToastUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class MusicListFragment extends BaseListFragment {

    public static MusicListFragment newInstance(FTType type) {
        MusicListFragment fragment = new MusicListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type.getValue());
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    protected void initView(View rootView) {

        gridView.setNumColumns(1);

        new MusicAsyncTask(new FTTaskCallback<List<Music>>() {
            @Override
            public void onPreExecute() {
                showProgressBar();
            }

            @Override
            public void onCancelled() {
                hideProgressBar();
            }

            @Override
            public void onFinished(List<Music> musics) {
                hideProgressBar();
                if (musics != null && musics.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(musics);
                    mFTInfoAdapter = new FTInfoAdapter(getContext(), musics, FTType.MUSIC);
                    gridView.setAdapter(mFTInfoAdapter);

                } else {
                    ToastUtils.showLongToast("暂时找不到应用的信息");
                }
            }
        }).executeOnExecutor(MAIN_EXECUTOR);
    }
}
