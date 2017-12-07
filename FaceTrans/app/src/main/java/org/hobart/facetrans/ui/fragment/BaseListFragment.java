package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.view.RecyclerViewItemDecoration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by huzeyin on 2017/11/27.
 */

public abstract class BaseListFragment extends Fragment {

    protected static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);
    private SwipeRefreshLayout refreshLayout;
    protected RecyclerView recycleView;
    private ProgressBar progressBar;
    protected RecyclerView.Adapter mAdapter;
    private View rootView;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (checkEnableFetchData()) {
            fetchData(false);
        }
    }

    protected boolean checkEnableFetchData() {
        return getUserVisibleHint() && (rootView != null) && (!isLoading()) && (!refreshLayout.isRefreshing());
    }

    protected abstract void fetchData(boolean isSwipeRefresh);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_file_list, container, false);

        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.srl_content);

        recycleView = (RecyclerView) rootView.findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleView.setHasFixedSize(true);
        recycleView.addItemDecoration(new RecyclerViewItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Log.d("hulaoda", "setOnRefreshListener ");

                if (getUserVisibleHint() && (rootView != null) && (!isLoading())) {
                    fetchData(true);
                } else {
                    refreshLayout.setRefreshing(false);
                }
            }
        });
        this.rootView = rootView;

        initView(rootView);

        return rootView;
    }

    protected abstract void initView(View view);

    @Override
    public void onResume() {
        updateFileInfoAdapter();
        super.onResume();
    }

    public void updateFileInfoAdapter() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    protected void updateSelectedView() {
        if (getActivity() != null && (getActivity() instanceof ChooseFileActivity)) {
            ChooseFileActivity chooseFileActivity = (ChooseFileActivity) getActivity();
            chooseFileActivity.getSelectedView();
        }
    }

    protected void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        if (progressBar.isShown()) {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected void stopRefreshing() {
        refreshLayout.setRefreshing(false);
    }

    private boolean isLoading() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

}
