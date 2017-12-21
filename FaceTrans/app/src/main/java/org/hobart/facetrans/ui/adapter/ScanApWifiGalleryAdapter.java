package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.ScanApUser;
import org.hobart.facetrans.ui.listener.OnRecyclerViewClickListener;

import java.util.List;

/**
 * Created by huzeyin on 2017/12/21.
 */

public class ScanApWifiGalleryAdapter extends RecyclerView.Adapter<ScanApWifiGalleryAdapter.ViewHolder> {

    private List<ScanApUser> mLists;
    private Context mContext;
    private OnRecyclerViewClickListener mListener;

    public ScanApWifiGalleryAdapter(Context context, List<ScanApUser> list, OnRecyclerViewClickListener listener) {
        mContext = context;
        mLists = list;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_ap, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ScanApUser scanApUser = mLists.get(position);
        Glide.with(mContext)
                .load(scanApUser.getUserIcon())
                .centerCrop()
                .placeholder(R.mipmap.icon_image_default)
                .crossFade()
                .into(holder.iv_shortcut);
        holder.tv_name.setText(scanApUser.getUserSSID());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.rootView, holder.rootView, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_shortcut;
        TextView tv_name;
        LinearLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = (LinearLayout) itemView.findViewById(R.id.rootView);
            iv_shortcut = (ImageView) itemView.findViewById(R.id.iv_shortcut);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
