package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.model.VideoFolder;
import org.hobart.facetrans.opengl.OpenGlUtils;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AndroidUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/30.
 */

public class VideoFileListAdapter extends RecyclerView.Adapter<VideoFileListAdapter.ViewHolder> {

    private List<VideoFolder> mVideoFolders;
    private OnRecyclerViewClickListener mListener;
    private Context mContext;

    private int folderIconLeftMargin;

    public VideoFileListAdapter(Context context, List<VideoFolder> datas, OnRecyclerViewClickListener listener) {
        mContext = context;
        mVideoFolders = datas;
        mListener = listener;
        if (null == mListener)
            throw new NullPointerException("回调接口不能为空!");
        folderIconLeftMargin = AndroidUtils.dip2px(10);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        VideoFolder videoFolder = mVideoFolders.get(position);
        holder.tv_fileName.setText(videoFolder.getFolderName());
        holder.tv_fileNums.setText(String.format(mContext.getResources().getString(R.string.photo_num), videoFolder.getFolderFileNum()));

        holder.iv_icon.setImageBitmap(videoFolder.getFirstVideoBitmap());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.mCardView, holder.iv_icon, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoFolders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_icon;
        public TextView tv_fileName;
        public TextView tv_fileNums;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_fileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tv_fileNums = (TextView) itemView.findViewById(R.id.tv_pic_nums);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            float ratio = OpenGlUtils.VIEW_W_H;
            int w = AndroidUtils.dip2px(70);
            int h = w;
            if (w / h > ratio) {
                w = (int) (h * ratio);
            } else if (w / h < ratio) {
                h = (int) (w / ratio);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
            params.leftMargin = folderIconLeftMargin;
            iv_icon.setLayoutParams(params);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }
    }
}
