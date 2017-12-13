package org.hobart.facetrans.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;
import org.hobart.facetrans.util.SimpleImageThumbnailLoader;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private List<Music> mMusics;
    private OnRecyclerViewClickListener mListener;

    public MusicListAdapter(List<Music> lists, OnRecyclerViewClickListener listener) {
        mMusics = lists;
        mListener = listener;
        if (null == mListener)
            throw new NullPointerException("回调接口不能为空!");
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_list_1, parent, false);

        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, int position) {

        Music music = mMusics.get(position);

        holder.tv_name.setText(music.getName() == null ? "" : music.getName());

        holder.tv_size.setText(music.getSizeDesc() == null ? "" : music.getSizeDesc());

        SimpleImageThumbnailLoader.getInstance().displayImageView(music.getFilePath(), FTType.MUSIC, holder.iv_shortcut, R.mipmap.icon_music_default);

        if (FTFileManager.getInstance().isFTFileExist(music)) {
            holder.rootView.setPressed(true);
        } else {
            holder.rootView.setPressed(false);
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.rootView, holder.rootView, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_size;
        RelativeLayout rootView;
        ImageView iv_shortcut;

        public MusicViewHolder(View itemView) {
            super(itemView);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            iv_shortcut = (ImageView) itemView.findViewById(R.id.iv_shortcut);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }

}
