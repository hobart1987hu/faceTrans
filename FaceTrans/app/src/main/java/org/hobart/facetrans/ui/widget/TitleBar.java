package org.hobart.facetrans.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hobart.facetrans.R;

/**
 * Created by huzeyin on 2017/11/21.
 */

public class TitleBar extends RelativeLayout {

    public TitleBar(Context context) {
        super(context);
        init();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private ImageView iv_back;
    private TextView tv_title;
    private Button btnRight;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_title_bar, this, true);
        iv_back = (ImageView) findViewById(R.id.titlebar_back);
        tv_title = (TextView) findViewById(R.id.titlebar_title);
        btnRight = (Button) findViewById(R.id.titlebar_btnRight);

        iv_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) mListener.onClick(v);
            }
        });

        btnRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) mListener.onClick(v);
            }
        });

    }

    private View.OnClickListener mListener;

    public void setViewOnClickListener(View.OnClickListener listener) {
        mListener = listener;
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setBtnRight(String text) {
        btnRight.setText(text);
        btnRight.setVisibility(View.VISIBLE);
    }
}
