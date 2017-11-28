package org.hobart.facetrans.ui.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.FileInfoEvent;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.ui.adapter.FileInfoSelectedAdapter;
import org.hobart.facetrans.util.FileUtils;

import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by huzeyin on 2017/11/24.
 */

public class ShowSelectedFileInfoDialog {
    /**
     * UI控件
     */
    @Bind(R.id.btn_operation)
    Button btn_operation;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.lv_result)
    ListView lv_result;
    Context mContext;
    AlertDialog mAlertDialog;
    FileInfoSelectedAdapter mSelectedAdapter;

    public ShowSelectedFileInfoDialog(Context context) {
        this.mContext = context;

        View contentView = View.inflate(mContext, R.layout.view_show_selected_file_info_dialog, null);

        ButterKnife.bind(this, contentView);

        String title = getAllSelectedFilesDes();
        tv_title.setText(title);

        mSelectedAdapter = new FileInfoSelectedAdapter(mContext);
        mSelectedAdapter.setOnDataListChangedListener(new FileInfoSelectedAdapter.OnDataListChangedListener() {
            @Override
            public void onDataChanged() {
                if (mSelectedAdapter.getCount() == 0) {
                    hide();
                }
                tv_title.setText(getAllSelectedFilesDes());
                EventBus.getDefault().post(new FileInfoEvent());
            }
        });

        lv_result.setAdapter(mSelectedAdapter);

        this.mAlertDialog = new AlertDialog.Builder(mContext)
                .setView(contentView)
                .create();
    }

    @OnClick({R.id.btn_operation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_operation: {
                clearAllSelectedFiles();
                EventBus.getDefault().post(new FileInfoEvent());
                break;
            }
        }
    }

    /**
     * 获取选中文件对话框的Title
     *
     * @return
     */
    private String getAllSelectedFilesDes() {
        String title = "";

        long totalSize = 0;
        Set<Map.Entry<String, FTFile>> entrySet = FTFileManager.getInstance().getFTFiles().entrySet();
        for (Map.Entry<String, FTFile> entry : entrySet) {
            FTFile fileInfo = entry.getValue();
            totalSize = totalSize + fileInfo.getSize();
        }
        title = mContext.getResources().getString(R.string.str_selected_file_info_detail)
                .replace("{count}", String.valueOf(entrySet.size()))
                .replace("{size}", String.valueOf(FileUtils.getFileSize(totalSize)));

        return title;
    }

    /**
     * 清除所有选中的文件
     */
    private void clearAllSelectedFiles() {
        FTFileManager.getInstance().getFTFiles().clear();
        if (mSelectedAdapter != null) {
            mSelectedAdapter.notifyDataSetChanged();
        }

        this.hide();
    }

    /**
     * 显示
     */
    public void show() {
        if (this.mAlertDialog != null) {
            notifyDataSetChanged();
            tv_title.setText(getAllSelectedFilesDes());
            this.mAlertDialog.show();
        }
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (this.mAlertDialog != null) {
            this.mAlertDialog.hide();
        }
    }

    /**
     * 通知列表发生变化
     */
    public void notifyDataSetChanged() {
        if (mSelectedAdapter != null) {
            mSelectedAdapter.notifyDataSetChanged();
        }
    }
}
