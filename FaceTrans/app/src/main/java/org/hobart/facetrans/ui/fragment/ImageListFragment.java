package org.hobart.facetrans.ui.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.ImageAsyncTask;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.adapter.ImageFileListAdapter;
import org.hobart.facetrans.ui.adapter.ImageGridApter;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AnimationUtils;
import org.hobart.facetrans.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class ImageListFragment extends BaseListFragment {

    private List<ImageFolder> mDataList = new ArrayList<>();

    @Override
    protected void fetchData(final boolean isSwipeRefresh) {
        new ImageAsyncTask(new FTTaskCallback<List<ImageFolder>>() {
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
            public void onFinished(List<ImageFolder> images) {
                if (isSwipeRefresh) {
                    stopRefreshing();
                } else {
                    hideProgressBar();
                }
                if (images != null && images.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(images);
                    mAdapter = new ImageFileListAdapter(getContext(), mDataList, new OnRecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View container, View view, int position) {

                            showImageFileListView(container, view, position);
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

    private ImageGridApter imageGridApter;

    private void showImageFileListView(final View container, final View view, int position) {

        final ChooseFileActivity parent = (ChooseFileActivity) getActivity();

        if (parent.isFlip()) return;
        parent.setFlip(true);

        final ImageFolder folder = mDataList.get(position);

        final List<Image> images = folder.getImages();
        if (null == images || images.size() <= 0) {
            parent.setFlip(false);
            return;
        }

        RecyclerView recyclerView = parent.getFileListRecycleView();

        imageGridApter = new ImageGridApter(getContext(), new OnRecyclerViewClickListener.SimpleOnRecyclerViewClickListener() {
            @Override
            public void onItemClick(View container, View view, int position) {
                FTFile ftFile = images.get(position);
                if (FTFileManager.getInstance().isFTFileExist(ftFile)) {
                    FTFileManager.getInstance().delFTFile(ftFile);
                    updateSelectedView();
                } else {
                    FTFileManager.getInstance().addFTFile(ftFile);
                    View startView = view;
                    View targetView = null;
                    if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
                        ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
                        targetView = chooseFileActivity.getSelectedView();
                    }
                    AnimationUtils.setAddTaskAnimation(getActivity(), startView, targetView, null);
                }
                imageGridApter.notifyItemChanged(position);
            }
        });
        imageGridApter.setDatas(images);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imageGridApter);

        parent.delayFlipPerspectiveView(view,1, container.getX(), container.getY(), view.getWidth(), view.getHeight(), folder.getFirstFilePath(), null);
    }
}
