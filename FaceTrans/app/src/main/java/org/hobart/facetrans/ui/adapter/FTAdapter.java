package org.hobart.facetrans.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.hobart.facetrans.model.FTFile;

import java.util.List;

public abstract class FTAdapter<T extends FTFile> extends android.widget.BaseAdapter {

    Context mContext;
    List<T> mDataList;

    public FTAdapter(Context context, List<T> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    /**
     * 添加数据源
     *
     * @param mDataList
     */
    public void addDataList(List<T> mDataList) {
        this.mDataList.addAll(mDataList);
        notifyDataSetChanged();
    }

    /**
     * 清除数据
     */
    public void clear() {
        this.mDataList.clear();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = convertView(position, convertView);
        return convertView;
    }

    /**
     * 重写convertView方法
     *
     * @param position
     * @param convertView
     * @return
     */
    public abstract View convertView(int position, View convertView);

}
