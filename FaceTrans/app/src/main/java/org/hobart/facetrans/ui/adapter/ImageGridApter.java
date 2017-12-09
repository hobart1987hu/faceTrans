package org.hobart.facetrans.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.opengl.ScreenJudgeImageView;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AndroidUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzeyin on 2017/11/30.
 */
public class ImageGridApter extends RecyclerView.Adapter<ImageGridApter.GridViewHolder> {

    private List<Image> mImages = new ArrayList<>();
    private Context mContext;
    private OnRecyclerViewClickListener mListener;
    private int margin2;
    private int mImageHeight;
    private int mImageWidth;

    public ImageGridApter(Context context, OnRecyclerViewClickListener listener) {
        mContext = context;
        mListener = listener;
        int screenWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        margin2 = AndroidUtils.dip2px(2);
        mImageHeight = AndroidUtils.dip2px(120);
        mImageWidth = screenWidth / 3 - (margin2 * 6 / 3);
    }

    public void setDatas(List<Image> list) {
        if (null != list && list.size() > 0) {
            mImages.clear();
            mImages.addAll(list);
        }
    }

    @Override
    public ImageGridApter.GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_grid, null);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageGridApter.GridViewHolder holder, final int position) {
        Image image = mImages.get(position);
        holder.iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(holder.containerView, holder.iv_pic, position);
            }
        });
        if (FTFileManager.getInstance().isFTFileExist(image)) {
            holder.iv_mask.setVisibility(View.VISIBLE);
        } else {
            holder.iv_mask.setVisibility(View.GONE);
        }
        Glide
                .with(mContext)
                .load(image.getFilePath())
                .placeholder(R.mipmap.icon_default)
                .crossFade()
                .centerCrop()
                .into(holder.iv_pic);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        public View containerView;
        public ImageView iv_pic;
        public ImageView iv_mask;
        public CardView mCardView;

        public GridViewHolder(View convertView) {
            super(convertView);
            containerView = convertView.findViewById(R.id.main_frame_layout);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mImageWidth, mImageHeight);
            params.leftMargin = params.topMargin = params.bottomMargin = params.rightMargin = margin2;
            iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            iv_pic.setLayoutParams(params);
            iv_mask = (ImageView) convertView.findViewById(R.id.iv_mask);
            mCardView = (CardView) convertView.findViewById(R.id.card_view);
        }
    }
}