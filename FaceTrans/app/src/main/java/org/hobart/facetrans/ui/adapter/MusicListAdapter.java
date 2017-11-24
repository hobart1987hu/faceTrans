package org.hobart.facetrans.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.hobart.facetrans.R;
import org.hobart.facetrans.model.Music;

/**
 * Created by huzeyin on 2017/11/21.
 */

public class MusicListAdapter extends BaseRecycleViewAdapter<Music> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        Music music = mDatas.get(position);

        viewHolder.textView.setText(music.getMusicName());

        if (music.isSelected()) {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setVisibility(View.GONE);
            viewHolder.checkBox.setChecked(false);
        }
    }

    class ViewHolder extends BaseViewHolder {

        private TextView textView;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
