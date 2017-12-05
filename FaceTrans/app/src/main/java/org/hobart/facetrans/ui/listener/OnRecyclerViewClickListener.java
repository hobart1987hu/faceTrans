package org.hobart.facetrans.ui.listener;

import android.view.View;

/**
 * Created by huzeyin on 2017/11/30.
 */

public interface OnRecyclerViewClickListener {
    /**
     * 点击item监听时间
     *
     * @param view     View
     * @param position position
     */
    void onItemClick(View container, View view, int position);

    /**
     * 长按监听时间
     *
     * @param view     View
     * @param position position
     */
    void onItemLongClick(View container, View view, int position);


    public static class SimpleOnRecyclerViewClickListener implements OnRecyclerViewClickListener {
        @Override
        public void onItemClick(View container, View view, int position) {

        }

        @Override
        public void onItemLongClick(View container, View view, int position) {

        }
    }
}

