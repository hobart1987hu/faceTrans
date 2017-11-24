package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.task.FTTaskCallback;
import org.hobart.facetrans.task.impl.MusicAsyncTask;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.adapter.BaseRecycleViewAdapter;
import org.hobart.facetrans.ui.adapter.MusicListAdapter;
import org.hobart.facetrans.ui.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/21.
 */

public class MusicListActivity extends BaseActivity implements FTTaskCallback<List<Music>> {

    private RecyclerView mRecycleView;
    private MusicListAdapter mAdapter;
    private ArrayList<Music> mDatas;

    private TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);

        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        initTitleBar();

        mDatas = new ArrayList<>();

        mRecycleView = (RecyclerView) findViewById(R.id.recycleView);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MusicListAdapter();
        mAdapter.setItemListener(new BaseRecycleViewAdapter.RecycleViewItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                Music music = mDatas.get(position);
                mDatas.get(position).setSelected(!music.isSelected());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnItemLongClickListener(View view, int position) {

            }
        });
        mRecycleView.setAdapter(mAdapter);
    }

    void initTitleBar() {
        mTitleBar.setTitle("我的音乐列表");
        mTitleBar.setBtnRight("发送");
        mTitleBar.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.titlebar_back) {
                    finish();
                } else {
                    //TODO:把已经选择的所有数据保存起来
                    List<Music> selectedDatas = new ArrayList<Music>();
                    for (Music music : mDatas) {
                        if (music.isSelected()) selectedDatas.add(music);
                    }
                    //TODO:
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MusicAsyncTask(this).execute();
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onExecute() {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onFinished(List<Music> musics) {
        if (null != musics && musics.size() > 0) {
            mDatas.clear();
            mDatas.addAll(musics);
            mAdapter.setDatas(mDatas, true);
        }
    }
}
