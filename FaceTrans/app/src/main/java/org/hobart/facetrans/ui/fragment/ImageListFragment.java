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
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.ImageAsyncTask;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.adapter.ImageFileListAdapter;
import org.hobart.facetrans.ui.adapter.ImageGridApter;
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

public class ImageListFragment extends Fragment {

    static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);

    private List<ImageFolder> mDataList = new ArrayList<>();
    private ImageFileListAdapter mFTInfoAdapter;
    private RecyclerView recycleView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_music_list, container, false);

        recycleView = (RecyclerView) rootView.findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setHasFixedSize(true);
        recycleView.addItemDecoration(new RecyclerViewItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        progressBar = (ProgressBar) rootView.findViewById(R.id.pb);

        initView();

        return rootView;
    }

    private void initView() {
        new ImageAsyncTask(new FTTaskCallback<List<ImageFolder>>() {
            @Override
            public void onPreExecute() {
                showProgressBar();
            }

            @Override
            public void onCancelled() {
                hideProgressBar();
            }

            @Override
            public void onFinished(List<ImageFolder> images) {
                hideProgressBar();
                if (images != null && images.size() > 0) {
                    mDataList.clear();
                    mDataList.addAll(images);
                    mFTInfoAdapter = new ImageFileListAdapter(getContext(), mDataList, new OnRecyclerViewClickListener() {
                        @Override
                        public void onItemClick(View container, View view, int position) {

                            showImageFileListView(container, view, position);
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

        parent.delayFlipPerspectiveView(view, container.getX(), container.getY(), view.getWidth(), view.getHeight(), folder.getFirstFilePath(), null);
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
