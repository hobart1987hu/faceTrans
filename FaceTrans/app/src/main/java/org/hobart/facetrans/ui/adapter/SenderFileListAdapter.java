package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.util.FileUtils;

import java.util.List;

/**
 * 发送文件列表
 * Created by huzeyin on 2017/11/28.
 */

public class SenderFileListAdapter extends RecyclerView.Adapter<SenderFileListAdapter.ViewHolder> {

    private List<TransferModel> mSendLists;

    private Context mContext;

    public SenderFileListAdapter(Context context, List<TransferModel> datas) {
        mContext = context;
        mSendLists = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_file_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TransferModel model = mSendLists.get(position);
        setTransferStatus(viewHolder.tv_transfer_status, model);
        viewHolder.tv_file_name.setText(model.fileName);
        viewHolder.tv_fileSize.setText(FileUtils.getFileSize(model.fileSize));
        Glide
                .with(mContext)
                .load(model.fileIcon)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .crossFade()
                .into(viewHolder.iv_fileIcon);
    }

    //设置传输状态
    private void setTransferStatus(TextView tv_transfer_status, TransferModel model) {
        int status = model.transferStatus;
        tv_transfer_status.setText("");
        switch (status) {
            case TransferStatus.UN_KNOW:
                break;
            case TransferStatus.WAITING:
                //等待中
                tv_transfer_status.setText("等待中");
                break;
            case TransferStatus.TRANSFERING:
                //进度中
                tv_transfer_status.setText(model.progress + "%");
                break;
            case TransferStatus.TRANSFER_SUCCESS:
                //已经完成
                tv_transfer_status.setText("已完成");
                break;
            case TransferStatus.TRANSFER_FAILED:
                //传输失败
                tv_transfer_status.setText("传输失败");
                break;
            case TransferStatus.ZIP:
                //压缩中
                tv_transfer_status.setText("压缩中");
                break;
            case TransferStatus.FAILED:
                tv_transfer_status.setText("失败");
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSendLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_fileIcon;

        TextView tv_file_name;

        TextView tv_fileSize;

        TextView tv_transfer_status;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_fileIcon = (ImageView) itemView.findViewById(R.id.iv_fileIcon);
            tv_file_name = (TextView) itemView.findViewById(R.id.tv_file_name);
            tv_fileSize = (TextView) itemView.findViewById(R.id.tv_fileSize);
            tv_transfer_status = (TextView) itemView.findViewById(R.id.tv_transfer_status);
        }
    }
}
