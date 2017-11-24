package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hobart.facetrans.R;
import org.hobart.facetrans.entity.HomeInfo;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.adapter.BaseRecycleViewAdapter;
import org.hobart.facetrans.ui.adapter.MyHomeAdapter;
import org.hobart.facetrans.ui.widget.HomeSlideView;
import org.hobart.facetrans.ui.widget.TitleBar;
import org.hobart.facetrans.util.IntentUtils;

import java.util.ArrayList;

/**
 * Created by huzeyin on 2017/11/14.
 */

public class HomeActivity extends BaseActivity {

    private HomeSlideView mHomeSlideView;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRecycleView;
    private TitleBar mTitleBar;

    private ArrayList<HomeInfo> mDatas = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mHomeSlideView = (HomeSlideView) findViewById(R.id.homeSlideView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mRecycleView = (RecyclerView) findViewById(R.id.recycleView);
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);

        initTitleBar();

        mDatas = new ArrayList<>();
        mDatas.add(new HomeInfo("我的音乐列表", HomeInfo.MUSIC_LIST));
        mDatas.add(new HomeInfo("我的视频列表", HomeInfo.VIDEO_LIST));
        mDatas.add(new HomeInfo("我的图片集", HomeInfo.IMAGE_LIST));
        mDatas.add(new HomeInfo("我的应用列表", HomeInfo.APP_LIST));

        MyHomeAdapter adapter = new MyHomeAdapter();
        adapter.setDatas(mDatas, false);
        adapter.setItemListener(new BaseRecycleViewAdapter.RecycleViewItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                HomeInfo info = mDatas.get(position);
                if (info.getPointer() == HomeInfo.MUSIC_LIST) {
                    IntentUtils.intentMusicListActivity(HomeActivity.this);
                }
            }

            @Override
            public void OnItemLongClickListener(View view, int position) {

            }
        });

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mRecycleView.setAdapter(adapter);
    }

    void initTitleBar() {
        mTitleBar.setTitle("首页");
        mTitleBar.setViewOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

