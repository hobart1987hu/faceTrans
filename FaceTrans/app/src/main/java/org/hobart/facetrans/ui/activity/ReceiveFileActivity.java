package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * 接收文件界面
 * Created by huzeyin on 2017/11/28.
 */

public class ReceiveFileActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_file);
        ButterKnife.bind(this);
    }
}
