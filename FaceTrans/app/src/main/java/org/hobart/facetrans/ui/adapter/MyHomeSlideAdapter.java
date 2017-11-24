package org.hobart.facetrans.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hobart.facetrans.R;
import org.hobart.facetrans.entity.HomeSlideInfo;

/**
 * Created by huzeyin on 2017/11/20.
 */

public class MyHomeSlideAdapter extends BaseRecycleViewAdapter<HomeSlideInfo> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_slide, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.textView.setText(mDatas.get(position).getValue());
    }

    public class ViewHolder extends BaseRecycleViewAdapter.BaseViewHolder {

        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
