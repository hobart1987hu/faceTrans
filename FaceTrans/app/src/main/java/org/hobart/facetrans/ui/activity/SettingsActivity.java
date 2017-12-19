package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.activity.base.BaseTitleBarActivity;
import org.hobart.facetrans.ui.dialog.ShowFileSavePathDialog;

/**
 * Created by huzeyin on 2017/12/18.
 */

public class SettingsActivity extends BaseTitleBarActivity {

    private ShowFileSavePathDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setCenterText("设置");

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
