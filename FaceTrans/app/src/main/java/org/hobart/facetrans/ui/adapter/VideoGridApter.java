package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.ScreenshotUtils;
import org.hobart.facetrans.util.SimpleImageThumbnailLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/30.
 */
public class VideoGridApter extends RecyclerView.Adapter<VideoGridApter.GridViewHolder> {

    private List<Video> mVideos = new ArrayList<>();
    private Context mContext;
    private OnRecyclerViewClickListener mListener;

    public VideoGridApter(Context context, OnRecyclerViewClickListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setDatas(List<Video> list) {
        if (null != list && list.size() > 0) {
            mVideos.clear();
            mVideos.addAll(list);
        }
    }

    @Override
    public VideoGridApter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_grid, null);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoGridApter.GridViewHolder holder, final int position) {
        Video video = mVideos.get(position);
        holder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder.containerView, holder.iv_pic, position);
            }
        });
        if (FTFileManager.getInstance().isFTFileExist(video)) {
            holder.iv_mask.setVisibility(View.VISIBLE);
        } else {
            holder.iv_mask.setVisibility(View.GONE);
        }
        SimpleImageThumbnailLoader.getInstance().displayImageView(video.getFilePath(), holder.iv_pic, R.mipmap.icon_default);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        public View containerView;
        public ImageView iv_pic;
        public ImageView iv_mask;
        public CardView mCardView;

        public GridViewHolder(View convertView) {
            super(convertView);
            containerView = convertView.findViewById(R.id.main_frame_layout);
            iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            iv_mask = (ImageView) convertView.findViewById(R.id.iv_mask);
            mCardView = (CardView) convertView.findViewById(R.id.card_view);
        }
    }
}