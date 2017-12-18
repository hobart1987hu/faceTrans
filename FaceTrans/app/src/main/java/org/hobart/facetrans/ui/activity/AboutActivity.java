package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;

/**
 * Created by huzeyin on 2017/12/18.
 */

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("关于");
        tv_title.setVisibility(View.VISIBLE);

        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
