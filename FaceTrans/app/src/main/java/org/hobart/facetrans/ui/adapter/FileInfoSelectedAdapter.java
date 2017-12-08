package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.util.FileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileInfoSelectedAdapter extends BaseAdapter {

    private Context mContext;
    private Map<String, FTFile> mDataHashMap;

    private List<Map.Entry<String, FTFile>> fileInfoMapList;
    private OnDataListChangedListener mOnDataListChangedListener;

    public FileInfoSelectedAdapter(Context mContext) {
        this.mContext = mContext;
        mDataHashMap = FTFileManager.getInstance().getFTFiles();
        fileInfoMapList = new ArrayList<>(mDataHashMap.entrySet());
        Collections.sort(fileInfoMapList, GlobalConfig.DEFAULT_COMPARATOR);
    }

    public void setOnDataListChangedListener(OnDataListChangedListener onDataListChangedListener) {
        this.mOnDataListChangedListener = onDataListChangedListener;
    }

    @Override
    public void notifyDataSetChanged() {
        mDataHashMap = FTFileManager.getInstance().getFTFiles();
        fileInfoMapList = new ArrayList<>(mDataHashMap.entrySet());
        Collections.sort(fileInfoMapList, GlobalConfig.DEFAULT_COMPARATOR);
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return fileInfoMapList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileInfoMapList.get(position).getValue();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FTFile fileInfo = (FTFile) getItem(position);

        FileSenderHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_transfer, null);
            viewHolder = new FileSenderHolder();
            viewHolder.iv_shortcut = (ImageView) convertView.findViewById(R.id.iv_shortcut);
            viewHolder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.iv_tick = (ImageView) convertView.findViewById(R.id.iv_tick);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FileSenderHolder) convertView.getTag();
        }
        if (fileInfo != null) {
            if (FileUtils.isApkFile(fileInfo.getFilePath()) || FileUtils.isVideoFile(fileInfo.getFilePath())) {
                //TODO:
//                viewHolder.iv_shortcut.setImageBitmap(fileInfo.getBitmap());
            } else if (FileUtils.isImageFile(fileInfo.getFilePath())) {
                Glide.with(mContext)
                        .load(fileInfo.getFilePath())
                        .centerCrop()
                        .placeholder(R.mipmap.ic_launcher)
                        .crossFade()
                        .into(viewHolder.iv_shortcut);
            } else if (FileUtils.isMusicFile(fileInfo.getFilePath())) {
                viewHolder.iv_shortcut.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.icon_music_default));
            }
            viewHolder.tv_path.setText(fileInfo.getFilePath());
            viewHolder.tv_size.setText(FileUtils.getFileSize(fileInfo.getSize()));

            viewHolder.iv_tick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FTFileManager.getInstance().getFTFiles().remove(fileInfo.getFilePath());
                    notifyDataSetChanged();
                    if (mOnDataListChangedListener != null)
                        mOnDataListChangedListener.onDataChanged();
                }
            });
        }

        return convertView;
    }

    static class FileSenderHolder {
        ImageView iv_shortcut;
        TextView tv_path;
        TextView tv_size;
        ImageView iv_tick;
    }

    public interface OnDataListChangedListener {
        void onDataChanged();
    }
}
