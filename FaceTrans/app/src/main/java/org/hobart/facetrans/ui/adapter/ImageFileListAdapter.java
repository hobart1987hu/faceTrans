package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.ImageFolder;
import org.hobart.facetrans.opengl.OpenGlUtils;
import org.hobart.facetrans.opengl.ScreenJudgeImageView;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.DateUtils;

import java.util.List;

/**
 * Created by huzeyin on 2017/11/30.
 */

public class ImageFileListAdapter extends RecyclerView.Adapter<ImageFileListAdapter.ViewHolder> {

    private List<ImageFolder> mImageFolders;
    private OnRecyclerViewClickListener mListener;
    private Context mContext;

    private int folderIconLeftMargin;

    public ImageFileListAdapter(Context context, List<ImageFolder> datas, OnRecyclerViewClickListener listener) {
        mContext = context;
        mImageFolders = datas;
        mListener = listener;
        if (null == mListener)
            throw new NullPointerException("回调接口不能为空!");
        folderIconLeftMargin = AndroidUtils.dip2px(10);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_list_2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ImageFolder imageFolder = mImageFolders.get(position);
        holder.tv_fileName.setText(imageFolder.getFolderName());
        holder.tv_fileNums.setText(String.format(mContext.getResources().getString(R.string.photo_num), imageFolder.getFolderFileNum()));

        Glide
                .with(mContext)
                .load(imageFolder.getFirstFilePath())
                .fitCenter()
                .placeholder(R.mipmap.icon_default)
                .crossFade()
                .into(holder.iv_icon);

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.rootView, holder.iv_icon, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageFolders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_icon;
        TextView tv_fileName;
        TextView tv_fileNums;
        RelativeLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_fileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tv_fileNums = (TextView) itemView.findViewById(R.id.tv_pic_nums);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            float ratio = OpenGlUtils.VIEW_W_H;
            int w = AndroidUtils.dip2px(120);
            int h = w;
            if (w / h > ratio) {
                w = (int) (h * ratio);
            } else if (w / h < ratio) {
                h = (int) (w / ratio);
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
            params.leftMargin = folderIconLeftMargin;
            iv_icon.setLayoutParams(params);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
        }
    }
}
