package org.hobart.facetrans.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private List<Music> mMusics;
    private OnRecyclerViewClickListener mListener;

    public MusicListAdapter(List<Music> lists, OnRecyclerViewClickListener listener) {
        mMusics = lists;
        mListener  = listener;
        if (null == mListener)
            throw new NullPointerException("回调接口不能为空!");
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mp3, parent, false);

        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, int position) {

        Music music = mMusics.get(position);

        holder.tv_name.setText(music.getName() == null ? "" : music.getName());

        holder.tv_size.setText(music.getSizeDesc() == null ? "" : music.getSizeDesc());

        if (FTFileManager.getInstance().isFTFileExist(music)) {

            holder.iv_ok_tick.setVisibility(View.VISIBLE);

        } else {

            holder.iv_ok_tick.setVisibility(View.GONE);
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.card_view, holder.card_view, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }

    class MusicViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
        CardView card_view;

        public MusicViewHolder(View itemView) {
            super(itemView);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
            iv_ok_tick = (ImageView) itemView.findViewById(R.id.iv_ok_tick);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }

}
