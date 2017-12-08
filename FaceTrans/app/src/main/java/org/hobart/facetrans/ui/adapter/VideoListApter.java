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

import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.Video;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.SimpleImageThumbnailLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/30.
 */
public class VideoListApter extends RecyclerView.Adapter<VideoListApter.VideListViewHolder> {

    private List<Video> mVideos = new ArrayList<>();
    private OnRecyclerViewClickListener mListener;

    public VideoListApter(OnRecyclerViewClickListener listener) {
        mListener = listener;
    }

    public void setDatas(List<Video> list) {
        if (null != list && list.size() > 0) {
            mVideos.clear();
            mVideos.addAll(list);
        }
    }

    @Override
    public VideoListApter.VideListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, null);
        return new VideListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoListApter.VideListViewHolder holder, final int position) {
        Video video = mVideos.get(position);
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder.containerView, holder.iv_shortcut, position);
            }
        });
        holder.tv_name.setText(video.getName());
        holder.tv_size.setText(video.getSizeDesc());
        holder.tv_duration.setText(FileUtils.formatVideoTime(video.getDuration()));
        if (FTFileManager.getInstance().isFTFileExist(video)) {
            holder.containerView.setPressed(true);
        } else {
            holder.containerView.setPressed(false);
        }
        SimpleImageThumbnailLoader.getInstance().displayImageView(video.getFilePath(), holder.iv_shortcut, R.mipmap.icon_default);
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    class VideListViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout containerView;
        ImageView iv_shortcut;
        TextView tv_duration;
        TextView tv_size;
        TextView tv_name;

        public VideListViewHolder(View convertView) {
            super(convertView);
            containerView = (RelativeLayout) convertView.findViewById(R.id.rootView);
            tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
            tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
        }
    }
}