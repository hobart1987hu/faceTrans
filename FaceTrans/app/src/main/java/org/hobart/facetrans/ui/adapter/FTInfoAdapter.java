package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.FTType;
import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;

import java.util.List;

public class FTInfoAdapter<T> extends FTAdapter {

    private FTType mType = FTType.APK;

    public FTInfoAdapter(Context context, List<T> dataList) {
        super(context, dataList);
    }

    public FTInfoAdapter(Context context, List<T> dataList, FTType type) {
        super(context, dataList);
        this.mType = type;
    }

    @Override
    public View convertView(int position, View convertView) {

        FTFile ftFile = (FTFile) getDataList().get(position);

        if (mType == FTType.APK) {
            ApkViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_apk, null);
                viewHolder = new ApkViewHolder();
                viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ApkViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {

                viewHolder.iv_shortcut.setImageBitmap(ftFile.getBitmap());
                viewHolder.tv_name.setText(ftFile.getName() == null ? "" : ftFile.getName());
                viewHolder.tv_size.setText(ftFile.getSizeDesc() == null ? "" : ftFile.getSizeDesc());

                if (FTFileManager.getInstance().isFTFileExist(ftFile)) {
                    viewHolder.iv_ok_tick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iv_ok_tick.setVisibility(View.GONE);
                }
            }
        } else if (mType == FTType.IMAGE) {
            ImageViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_jpg, null);
                viewHolder = new ImageViewHolder();
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ImageViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {

                Glide
                        .with(getContext())
                        .load(ftFile.getFilePath())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(viewHolder.iv_shortcut);

                if (FTFileManager.getInstance().isFTFileExist(ftFile)) {
                    viewHolder.iv_ok_tick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iv_ok_tick.setVisibility(View.GONE);
                }
            }
        } else if (mType == FTType.MUSIC) {
            MusicViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_mp3, null);
                viewHolder = new MusicViewHolder();
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MusicViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {
                viewHolder.tv_name.setText(ftFile.getName() == null ? "" : ftFile.getName());
                viewHolder.tv_size.setText(ftFile.getSizeDesc() == null ? "" : ftFile.getSizeDesc());

                if (FTFileManager.getInstance().isFTFileExist(ftFile)) {
                    viewHolder.iv_ok_tick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iv_ok_tick.setVisibility(View.GONE);
                }
            }
        } else if (mType == FTType.VIDEO) {
            VideoViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.item_mp4, null);
                viewHolder = new VideoViewHolder();
                viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
                viewHolder.iv_ok_tick = (ImageView) convertView.findViewById(R.id.iv_ok_tick);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (VideoViewHolder) convertView.getTag();
            }

            if (getDataList() != null && getDataList().get(position) != null) {
                viewHolder.iv_shortcut.setImageBitmap(ftFile.getBitmap());
                viewHolder.tv_name.setText(ftFile.getName() == null ? "" : ftFile.getName());
                viewHolder.tv_size.setText(ftFile.getSizeDesc() == null ? "" : ftFile.getSizeDesc());

                if (FTFileManager.getInstance().isFTFileExist(ftFile)) {
                    viewHolder.iv_ok_tick.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.iv_ok_tick.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }

    static class ApkViewHolder {
        ImageView iv_shortcut;
        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
    }

    static class ImageViewHolder {
        ImageView iv_shortcut;
        ImageView iv_ok_tick;
    }

    static class MusicViewHolder {
        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
    }

    static class VideoViewHolder {
        ImageView iv_shortcut;
        ImageView iv_ok_tick;
        TextView tv_name;
        TextView tv_size;
    }
}
