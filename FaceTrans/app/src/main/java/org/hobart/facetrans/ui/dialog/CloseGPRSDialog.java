package org.hobart.facetrans.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import org.hobart.facetrans.R;

/**
 * 关闭数据流量dialog
 * Created by huzeyin on 2017/12/15.
 */

public abstract class CloseGPRSDialog {

    private AlertDialog mAlertDialog;
    private Context mContext;

    public CloseGPRSDialog(Context context) {
        this.mContext = context;
        View contentView = View.inflate(mContext, R.layout.view_close_gprs_dialog, null);
        this.mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(contentView)
                .create();
        contentView.findViewById(R.id.tv_close_grps).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeGPRS();
            }
        });
        contentView.findViewById(R.id.tv_use_gprs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useGPRS();
            }
        });
    }

    public void show() {
        if (this.mAlertDialog != null) {
            this.mAlertDialog.show();
        }
    }

    public void hide() {
        if (this.mAlertDialog != null) {
            this.mAlertDialog.hide();
        }
    }

    public abstract void closeGPRS();

    public abstract void useGPRS();
}
