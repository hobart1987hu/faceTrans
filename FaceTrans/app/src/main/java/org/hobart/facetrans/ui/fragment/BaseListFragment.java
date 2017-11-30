package org.hobart.facetrans.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.adapter.FTInfoAdapter;
import org.hobart.facetrans.util.AnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by huzeyin on 2017/11/27.
 */

public abstract class BaseListFragment extends Fragment {

    static Executor MAIN_EXECUTOR = Executors.newFixedThreadPool(5);

    @Bind(R.id.gridView)
    GridView gridView;
    @Bind(R.id.pb)
    ProgressBar progressBar;

    protected FTType mType = FTType.APK;
    protected List<FTFile> mDataList = new ArrayList<>();
    protected FTInfoAdapter mFTInfoAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        int type = bundle.getInt("type");
        mType = FTType.getType(type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file_list, container, false);

        ButterKnife.bind(this, rootView);

        initView(rootView);

        initListener();

        return rootView;
    }

    protected abstract void initView(View rootView);


    private void initListener() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                mFTInfoAdapter.notifyDataSetChanged();
            }
        });
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
