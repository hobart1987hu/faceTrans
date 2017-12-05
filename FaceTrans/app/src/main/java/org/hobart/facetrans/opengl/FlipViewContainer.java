package org.hobart.facetrans.opengl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by huzeyin on 2017/12/3.
 */

public class FlipViewContainer extends FrameLayout {


    public FlipViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        OpenGlUtils.VIEW_W_H = ((float) (right - left)) / ((float) (bottom - top));
    }
}
