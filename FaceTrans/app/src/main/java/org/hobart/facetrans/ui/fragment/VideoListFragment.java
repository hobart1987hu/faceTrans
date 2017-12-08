package org.hobart.facetrans.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.model.VideoFolder;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.VideoAsyncTask;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.adapter.VideoFileListAdapter;
import org.hobart.facetrans.ui.adapter.VideoGridApter;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AnimationUtils;
import org.hobart.facetrans.util.SimpleImageThumbnailLoader;
import org.hobart.facetrans.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class VideoListFragment extends BaseListFragment {

    private List<VideoFolder> mDataList = new ArrayList<>();
    private VideoGridApter videoGridApter;
    private List<Video> videos;

    @Override
    protected void fetchData(final boolean isSwipeRefresh) {
        new VideoAsyncTask(new FTTaskCallback<List<VideoFolder>>() {
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
            public void onFinished(List<VideoFolder> videoFolders) {
                if (isSwipeRefresh) {
                    stopRefreshing();
                } else {
                    hideProgressBar();
                }
                if (videoFolders != null && videoFolders.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(videoFolders);
                    mAdapter = new VideoFileListAdapter(getContext(), mDataList, new OnRecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View container, View view, int position) {

                            showVideoFileListView(container, view, position);
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

    private void showVideoFileListView(final View container, final View view, int position) {

        videos = null;
        videoGridApter = null;

        final ChooseFileActivity parent = (ChooseFileActivity) getActivity();

        if (parent.isFlip()) return;
        parent.setFlip(true);

        final VideoFolder folder = mDataList.get(position);

        videos = folder.getVideos();
        if (null == videos || videos.size() <= 0) {
            return;
        }

        RecyclerView recyclerView = parent.getFileListRecycleView();

        videoGridApter = new VideoGridApter(getContext(), new OnRecyclerViewClickListener.SimpleOnRecyclerViewClickListener() {
            @Override
            public void onItemClick(View container, View view, int position) {
                FTFile ftFile = videos.get(position);
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
                videoGridApter.notifyItemChanged(position);
            }
        });
        videoGridApter.setDatas(videos);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(videoGridApter);

        Bitmap bitmap = SimpleImageThumbnailLoader.getInstance().getBitmapToMemoryCache(folder.getFolderIconPath());

        if (null == bitmap) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_default);
        }
        parent.delayFlipPerspectiveView(view, container.getX(), container.getY(), view.getWidth(), view.getHeight(), "", bitmap);
    }

}
