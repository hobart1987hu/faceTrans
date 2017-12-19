package org.hobart.facetrans.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hobart.facetrans.R;

/**
 * Created by huzeyin on 2017/12/19.
 */

public class TitleBar extends RelativeLayout {

    public static final int TYPE_BACK = 1;

    public TitleBar(Context context) {
        super(context);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private TextView tv_title;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_title_bar, this, true);
        findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mClick) mClick.onClick(TYPE_BACK);
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    private ViewOnClick mClick;

    public void setViewOnClick(ViewOnClick click) {
        mClick = click;
    }

    public interface ViewOnClick {
        void onClick(int type);
    }

    public void setCenterText(String text) {
        tv_title.setText(text);
    }
}
