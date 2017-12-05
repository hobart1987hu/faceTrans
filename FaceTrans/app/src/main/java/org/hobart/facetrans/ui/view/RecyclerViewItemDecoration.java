package org.hobart.facetrans.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hobart.facetrans.R;

/**
 * Created by huzeyin on 2017/12/5.
 */

public class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

    public static final int LINEAR_LAYOUT_ORIENTATION_VERTICAL = 0;
    public static final int LINEAR_LAYOUT_ORIENTATION_HORIZONTAL = 1;
    public static final int GRID_LAYOUT_ORIENTATION_VERTICAL = 2;
    public static final int GRID_LAYOUT_ORIENTATION_HORIZONTAL = 3;

    private int orientation = -1;
    private int rawOrColumnSum = 0;
    private Drawable myDivider = null;

    public RecyclerViewItemDecoration(Context context, int orientation) {

        myDivider = context.getResources().getDrawable(R.drawable.recycle_view_divider);

        if (orientation == LinearLayoutManager.HORIZONTAL) {
            this.orientation = LINEAR_LAYOUT_ORIENTATION_HORIZONTAL;
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            this.orientation = LINEAR_LAYOUT_ORIENTATION_VERTICAL;
        }

    }

    public RecyclerViewItemDecoration(Context context, int orientation, int rawOrColumnSum) {
        myDivider = context.getResources().getDrawable(R.drawable.recycle_view_divider);

        if (orientation == GridLayoutManager.HORIZONTAL) {
            this.orientation = GRID_LAYOUT_ORIENTATION_HORIZONTAL;
        } else if (orientation == GridLayoutManager.VERTICAL) {
            this.orientation = GRID_LAYOUT_ORIENTATION_VERTICAL;
        }
        this.rawOrColumnSum = rawOrColumnSum;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (orientation == LINEAR_LAYOUT_ORIENTATION_HORIZONTAL ||
                orientation == LINEAR_LAYOUT_ORIENTATION_VERTICAL) {
            linearLayoutDrawItemDecoration(c, parent);
        } else if (orientation == GRID_LAYOUT_ORIENTATION_HORIZONTAL ||
                orientation == GRID_LAYOUT_ORIENTATION_VERTICAL) {
            gridLayoutItemDecoration(c, parent);
        }

    }

    private void linearLayoutDrawItemDecoration(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int left, top, right, bottom;
        View child = parent.getChildAt(0);
        int drawableHeight = myDivider.getIntrinsicHeight();
        if (orientation == LINEAR_LAYOUT_ORIENTATION_VERTICAL) {
            left = parent.getLeft();
            right = parent.getRight();
            for (int i = 1; i < childCount; i++) {
                top = child.getBottom() - drawableHeight / 2;
                bottom = child.getBottom() + drawableHeight / 2;
                myDivider.setBounds(left, top, right, bottom);
                myDivider.draw(canvas);
                child = parent.getChildAt(i);
            }
        } else if (orientation == LINEAR_LAYOUT_ORIENTATION_HORIZONTAL) {
            top = child.getTop();
            bottom = child.getBottom();
            for (int i = 1; i < childCount; i++) {
                left = child.getRight() - drawableHeight / 2;
                right = child.getRight() + drawableHeight / 2;
                myDivider.setBounds(left, top, right, bottom);
                myDivider.draw(canvas);
                child = parent.getChildAt(i);
            }
        }
    }

    private void gridLayoutItemDecoration(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int lineSum = childCount / rawOrColumnSum - 1;
        lineSum += childCount % rawOrColumnSum == 0 ? 0 : 1;
        int drawableHeight = myDivider.getIntrinsicHeight();
        int left, right, top, bottom;
        View child = parent.getChildAt(0);

        if (orientation == GRID_LAYOUT_ORIENTATION_VERTICAL) {
            left = parent.getLeft();
            right = parent.getRight();
            for (int i = 0; i < lineSum; i++) {
                child = parent.getChildAt(i * rawOrColumnSum);
                top = child.getBottom() - drawableHeight / 2;
                bottom = child.getBottom() + drawableHeight / 2;
                myDivider.setBounds(left, top, right, bottom);
                myDivider.draw(canvas);
            }
            top = parent.getTop();
            bottom = parent.getBottom();
            for (int i = 0; i < rawOrColumnSum - 1; i++) {
                child = parent.getChildAt(i);
                left = child.getRight() - drawableHeight / 2;
                right = child.getRight() + drawableHeight / 2;
                myDivider.setBounds(left, top, right, bottom);
                myDivider.draw(canvas);
            }
        } else if (orientation == GRID_LAYOUT_ORIENTATION_HORIZONTAL) {
            top = parent.getTop();
            bottom = parent.getBottom();
            for (int i = 0; i <= lineSum; i++) {
                child = parent.getChildAt(i * rawOrColumnSum);
                left = child.getRight() - drawableHeight / 2;
                right = child.getRight() + drawableHeight / 2;
                myDivider.setBounds(left, top, right, bottom);
                myDivider.draw(canvas);
            }
            left = parent.getLeft();
            right = parent.getRight();
            for (int i = 0; i < rawOrColumnSum; i++) {
                child = parent.getChildAt(i);
                top = child.getBottom() - drawableHeight / 2;
                bottom = child.getBottom() + drawableHeight / 2;
                myDivider.setBounds(left, top, right, bottom);
                myDivider.draw(canvas);
            }
        }
    }

}
