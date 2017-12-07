package org.hobart.facetrans.ui.fragment;

import android.view.View;

import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.Apk;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.ApkAsyncTask;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.adapter.ApkListAdapter;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AnimationUtils;
import org.hobart.facetrans.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class ApkListFragment extends BaseListFragment {

    private List<Apk> mDataList = new ArrayList<>();

    @Override
    protected void fetchData(final boolean isSwipeRefresh) {

        new ApkAsyncTask(new FTTaskCallback<List<Apk>>() {
            @Override
            public void onPreExecute() {
                if (!isSwipeRefresh) {
                    showProgressBar();
                }
            }

            @Override
            public void onCancelled() {
                if (isSwipeRefresh) {
                    stopRefreshing();
                } else {
                    hideProgressBar();
                }
            }

            @Override
            public void onFinished(List<Apk> apkList) {

                if (isSwipeRefresh) {
                    stopRefreshing();
                } else {
                    hideProgressBar();
                }
                if (apkList != null && apkList.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(apkList);
                    mAdapter = new ApkListAdapter(mDataList, new OnRecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View container, View view, int position) {

                            FTFile ftFile = mDataList.get(position);
                            if (FTFileManager.getInstance().isFTFileExist(ftFile)) {
                                FTFileManager.getInstance().delFTFile(ftFile);
                                updateSelectedView();
                            } else {
                                FTFileManager.getInstance().addFTFile(ftFile);
                                View startView = null;
                                View targetView = null;
                                startView = view.findViewById(R.id.iv_shortcut);
                                if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
                                    ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
                                    targetView = chooseFileActivity.getSelectedView();
                                }
                                AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onItemLongClick(View container, View view, int position) {

                        }
                    });
                    recycleView.setAdapter(mAdapter);

                } else {
                    ToastUtils.showLongToast("暂时找不到应用的信息");
                }
            }
        }).executeOnExecutor(MAIN_EXECUTOR);
    }

    @Override
    protected void initView(View view) {
        if (checkEnableFetchData()) fetchData(false);
    }
}
