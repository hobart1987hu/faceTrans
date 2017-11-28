package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;

import butterknife.ButterKnife;

/**
 * 发送文件界面
 * Created by huzeyin on 2017/11/28.
 */

public class SendFileActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        ButterKnife.bind(this);
    }
}
