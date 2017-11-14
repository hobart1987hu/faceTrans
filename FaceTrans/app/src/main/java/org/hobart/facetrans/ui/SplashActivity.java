package org.hobart.facetrans.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import org.hobart.facetrans.R;
import org.hobart.facetrans.util.IntentUtils;

/**
 * Created by huzeyin on 2017/11/14.
 */

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentUtils.intentToHomeActivity(SplashActivity.this);
            }
        }, 2 * 1000);
    }
}
