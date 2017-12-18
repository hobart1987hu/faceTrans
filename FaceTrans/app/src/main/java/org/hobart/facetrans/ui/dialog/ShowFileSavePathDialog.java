package org.hobart.facetrans.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import org.hobart.facetrans.R;

/**
 * 关闭数据流量dialog
 * Created by huzeyin on 2017/12/15.
 */

public class ShowFileSavePathDialog {

    private AlertDialog mAlertDialog;
    private Context mContext;

    public ShowFileSavePathDialog(Context context) {
        this.mContext = context;
        View contentView = View.inflate(mContext, R.layout.view_show_save_path_dialog, null);
        this.mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(contentView)
                .create();
        contentView.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
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

    public boolean isShow() {
        return this.mAlertDialog.isShowing();
    }
}
