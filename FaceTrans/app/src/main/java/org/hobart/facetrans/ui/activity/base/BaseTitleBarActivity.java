package org.hobart.facetrans.ui.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.widget.TitleBar;

/**
 * Created by huzeyin on 2017/12/19.
 */

public class BaseTitleBarActivity extends BaseActivity {

    private LinearLayout mContainer;
    protected TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initializeView();
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        this.setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mContainer.addView(view);
    }

    private final void initView() {
        super.setContentView(R.layout.activity_base);
        mContainer = (LinearLayout) findViewById(R.id.root);
    }

    private final void initializeView() {
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        mTitleBar.setViewOnClick(new TitleBar.ViewOnClick() {
            @Override
            public void onClick(int type) {
                if (!handleViewOnClick()) {
                    finish();
                }
            }
        });
    }

    protected boolean handleViewOnClick() {
        return false;
    }

    protected void setEnableTitleBar(boolean enableTitleBar) {
        mTitleBar.setVisibility(enableTitleBar ? View.VISIBLE : View.GONE);
    }

    protected void setCenterText(String text) {
        mTitleBar.setCenterText(text);
    }

    protected void setContainerBackGround(int color) {
        mContainer.setBackgroundColor(color);
    }
}
