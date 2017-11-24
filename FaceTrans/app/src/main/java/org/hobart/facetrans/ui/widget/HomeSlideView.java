package org.hobart.facetrans.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.hobart.facetrans.R;
import org.hobart.facetrans.entity.HomeSlideInfo;
import org.hobart.facetrans.ui.adapter.BaseRecycleViewAdapter;
import org.hobart.facetrans.ui.adapter.MyHomeSlideAdapter;
import org.hobart.facetrans.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面 侧边栏 显示的内容有如下：
 * <p>
 * 1、扫面二维码接收文件->打开进入扫描二维码界面
 * 2、创建Wi-Fi热点,wifi 热点创建好之后，生成一个二维码图片，这个可以保存起来，后面可以使用
 * 3、当前版本号
 * Created by huzeyin on 2017/11/15.
 */

public class HomeSlideView extends LinearLayout {

    public HomeSlideView(Context context) {
        super(context);
        init();
    }

    public HomeSlideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeSlideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private RecyclerView mRecycleView;
    private ArrayList<HomeSlideInfo> infos;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_home_slide, this, true);
        mRecycleView = (RecyclerView) findViewById(R.id.recycleView);
        infos = new ArrayList<>();
        infos.add(new HomeSlideInfo("二维码扫描", HomeSlideInfo.QR_SCANNING));
        infos.add(new HomeSlideInfo("创建Wi-Fi热点", HomeSlideInfo.CREATE_WIFI_HOT));

        MyHomeSlideAdapter adapter = new MyHomeSlideAdapter();
        adapter.setDatas(infos, false);
        adapter.setItemListener(new BaseRecycleViewAdapter.RecycleViewItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                HomeSlideInfo info = infos.get(position);
                final int pointer = info.getPointer();
                if (pointer == HomeSlideInfo.QR_SCANNING) {
                    //二维码扫描
                    ToastUtils.showLongToast("二维码扫描");
                } else if (pointer == HomeSlideInfo.CREATE_WIFI_HOT) {
                    //创建Wi-Fi热点
                    ToastUtils.showLongToast("创建Wi-Fi热点");
                }
            }

            @Override
            public void OnItemLongClickListener(View view, int position) {

            }
        });
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleView.setAdapter(adapter);
        setGravity(Gravity.CENTER_HORIZONTAL);
    }
}
