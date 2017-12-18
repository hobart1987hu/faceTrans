package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.dialog.ShowFileSavePathDialog;

/**
 * Created by huzeyin on 2017/12/18.
 */

public class SettingsActivity extends BaseActivity {

    private ShowFileSavePathDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("设置");
        tv_title.setVisibility(View.VISIBLE);

        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.rl_showSavePath).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != null && mDialog.isShow()) return;
                if (null == mDialog) {
                    mDialog = new ShowFileSavePathDialog(SettingsActivity.this);
                }
                mDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != null && mDialog.isShow()) mDialog.hide();
    }
}
