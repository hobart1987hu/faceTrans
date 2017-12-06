package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import org.hobart.facetrans.ui.view.RecyclerViewItemDecoration;
import org.hobart.facetrans.util.AnimationUtils;
import org.hobart.facetrans.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by huzeyin on 2017/11/27.
 */

public class VideoListFragment extends Fragment {
    static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);
    private List<VideoFolder> mDataList = new ArrayList<>();
    private VideoFileListAdapter mFTInfoAdapter;
    private RecyclerView recycleView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_video_list, container, false);

        recycleView = (RecyclerView) rootView.findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setHasFixedSize(true);
        recycleView.addItemDecoration(new RecyclerViewItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        progressBar = (ProgressBar) rootView.findViewById(R.id.pb);

        initView();

        return rootView;
    }

    private void initView() {
        new VideoAsyncTask(new FTTaskCallback<List<VideoFolder>>() {
            @Override
            public void onPreExecute() {
                showProgressBar();
            }

            @Override
            public void onCancelled() {
                hideProgressBar();
            }

            @Override
            public void onFinished(List<VideoFolder> videoFolders) {
                hideProgressBar();
                if (videoFolders != null && videoFolders.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(videoFolders);
                    mFTInfoAdapter = new VideoFileListAdapter(getContext(), mDataList, new OnRecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View container, View view, int position) {

                            showVideoFileListView(container, view, position);
                        }

                        @Override
                        public void onItemLongClick(View container, View view, int position) {

                        }
                    });
                    recycleView.setAdapter(mFTInfoAdapter);

                } else {
                    ToastUtils.showLongToast("暂时找不到应用的信息");
                }
            }
        }).executeOnExecutor(MAIN_EXECUTOR);
    }

    private VideoGridApter videoGridApter;
    private List<Video> videos;

    private void showVideoFileListView(final View container, final View view, int position) {

        videos = null;
        videoGridApter = null;

        final ChooseFileActivity parent = (ChooseFileActivity) getActivity();

        if (parent.isFlip()) return;
        parent.setFlip(true);

        final VideoFolder folder = mDataList.get(position);

        videos = folder.getVideos();
        if (null == videos || videos.size() <= 0) {
            if (folder.isLoadAllVideo()) {
                parent.setFlip(false);
                return;
            }
            ToastUtils.showLongToast("正在加载数据----");
            List<Video> tempVideos = VideoAsyncTask.queryFolderVideos(folder.getFolderPath());
            if (null == tempVideos || tempVideos.size() <= 0) {
                folder.setLoadAllVideo(true);
                hideProgressBar();
                return;
            }
            folder.setVideos(tempVideos);
            videos = tempVideos;
            ToastUtils.showLongToast("数据加载完成----");
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

        parent.delayFlipPerspectiveView(view, container.getX(), container.getY(), view.getWidth(), view.getHeight(), "", folder.getFirstVideoBitmap());
    }

    @Override
    public void onResume() {
        updateFileInfoAdapter();
        super.onResume();
    }

    public void updateFileInfoAdapter() {
        if (mFTInfoAdapter != null)
            mFTInfoAdapter.notifyDataSetChanged();
    }

    private void updateSelectedView() {
        if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
            ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
            chooseFileActivity.getSelectedView();
        }
    }

    protected void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    protected void hideProgressBar() {
        if (progressBar != null && progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
