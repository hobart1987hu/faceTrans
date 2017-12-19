package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.activity.base.BaseTitleBarActivity;

/**
 * Created by huzeyin on 2017/12/18.
 */

public class AboutActivity extends BaseTitleBarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setCenterText("关于");
    }
}
