package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.TransferModel;
import org.hobart.facetrans.socket.transfer.TransferStatus;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.FileUtils;

import java.util.List;

/**
 * 接收文件列表
 * Created by huzeyin on 2017/11/28.
 */

public class ReceiveFileListAdapter extends RecyclerView.Adapter<ReceiveFileListAdapter.ViewHolder> {

    private List<TransferModel> mReceiveLists;

    private Context mContext;

    private OnRecyclerViewClickListener mListener;

    public ReceiveFileListAdapter(Context context, List<TransferModel> datas, OnRecyclerViewClickListener listener) {
        mContext = context;
        mReceiveLists = datas;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive_file_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(viewHolder.rootView, viewHolder.rootView, position);
            }
        });

        TransferModel model = mReceiveLists.get(position);
        setTransferStatus(viewHolder.tv_transfer_status, model);
        viewHolder.tv_file_name.setText(model.fileName);
        viewHolder.tv_fileSize.setText(FileUtils.getFileSize(model.fileSize));

        if (model.type == TransferModel.TYPE_APK) {
            if (model.transferStatus == TransferStatus.FINISH) {
                try {
                    viewHolder.iv_fileIcon.setImageDrawable(AndroidUtils.getApkIcon(model.fileIcon));
                } catch (Exception e) {
                    viewHolder.iv_fileIcon.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                viewHolder.iv_fileIcon.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            Glide
                    .with(mContext)
                    .load(model.fileIcon)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher)
                    .crossFade()
                    .into(viewHolder.iv_fileIcon);
        }
    }

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
                //失败
                tv_transfer_status.setText("传输失败");
                break;
            case TransferStatus.UNZIP:
                //解压中
                tv_transfer_status.setText("解压中");
                break;
            case TransferStatus.FINISH:
                //所有都完成
                tv_transfer_status.setText("完成");
                break;
            case TransferStatus.FAILED:
                //最终失败了
                tv_transfer_status.setText("失败");
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mReceiveLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_fileIcon;

        TextView tv_file_name;

        TextView tv_fileSize;

        TextView tv_transfer_status;

        RelativeLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            iv_fileIcon = (ImageView) itemView.findViewById(R.id.iv_fileIcon);
            tv_file_name = (TextView) itemView.findViewById(R.id.tv_file_name);
            tv_fileSize = (TextView) itemView.findViewById(R.id.tv_fileSize);
            tv_transfer_status = (TextView) itemView.findViewById(R.id.tv_transfer_status);
        }
    }
}
