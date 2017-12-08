package org.hobart.facetrans.ui.adapter;

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
import org.hobart.facetrans.model.Apk;
import org.hobart.facetrans.model.Image;
import org.hobart.facetrans.model.Music;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;

import java.util.List;

public class ApkListAdapter extends RecyclerView.Adapter<ApkListAdapter.MusicViewHolder> {

    private List<Apk> mMusics;
    private OnRecyclerViewClickListener mListener;

    public ApkListAdapter(List<Apk> lists, OnRecyclerViewClickListener listener) {
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

        Apk apk = mMusics.get(position);

        holder.tv_name.setText(apk.getName() == null ? "" : apk.getName());

        holder.tv_size.setText(apk.getSizeDesc() + "  版本号：" + apk.getVersionName());

        if (FTFileManager.getInstance().isFTFileExist(apk)) {

            holder.rootView.setPressed(true);

        } else {
            holder.rootView.setPressed(false);
        }

        holder.iv_shortcut.setImageDrawable(apk.getDrawable());

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
            iv_shortcut = (ImageView) itemView.findViewById(R.id.iv_shortcut);
            rootView = (RelativeLayout) itemView.findViewById(R.id.rootView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }

}
