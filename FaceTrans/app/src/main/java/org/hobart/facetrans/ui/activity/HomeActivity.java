package org.hobart.facetrans.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.ui.dialog.CloseGPRSDialog;
import org.hobart.facetrans.ui.widget.MyScrollView;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.IntentUtils;
import org.hobart.facetrans.util.NetworkUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.WifiHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by huzeyin on 2017/11/14.
 */

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, MyScrollView.OnScrollListener {

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    TextView tv_name;

    @Bind(R.id.ll_mini_main)
    LinearLayout ll_mini_main;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.iv_mini_avator)
    ImageView iv_mini_avator;
    @Bind(R.id.btn_send)
    Button btn_send;
    @Bind(R.id.btn_receive)
    Button btn_receive;

    @Bind(R.id.msv_content)
    MyScrollView mScrollView;
    @Bind(R.id.ll_main)
    LinearLayout ll_main;
    @Bind(R.id.btn_send_big)
    Button btn_send_big;
    @Bind(R.id.btn_receive_big)
    Button btn_receive_big;

    @Bind(R.id.rl_device)
    RelativeLayout rl_device;
    @Bind(R.id.tv_device_desc)
    TextView tv_device_desc;
    @Bind(R.id.rl_file)
    RelativeLayout rl_file;
    @Bind(R.id.tv_file_desc)
    TextView tv_file_desc;
    @Bind(R.id.rl_storage)
    RelativeLayout rl_storage;
    @Bind(R.id.tv_storage_desc)
    TextView tv_storage_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            finish();
                            return;
                        }
                        init();
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.System.canWrite(HomeActivity.this)) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showLongToast("检测到您未开启系统权限，请先开启!");
                                        AndroidUtils.requestWriteSettings(HomeActivity.this, REQUEST_CODE_WRITE_SETTINGS);
                                    }
                                }, 1000);
                            }
                        }
                    }
                });
    }


    private void init() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        String device = TextUtils.isEmpty(android.os.Build.DEVICE) ? GlobalConfig.AP_SSID : android.os.Build.DEVICE;
        try {
            tv_name = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.tv_name);
            tv_name.setText(device);
        } catch (Exception e) {
            //maybe occur some exception
        }
        mScrollView.setOnScrollListener(this);
        ll_mini_main.setClickable(false);
        ll_mini_main.setVisibility(View.GONE);

    }

    private boolean mIsExist = false;
    private Handler mHandler = new Handler();

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                if (mIsExist) {
                    this.finish();
                } else {
                    ToastUtils.showLongToast("再按一次就退出面传App");
                    mIsExist = true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsExist = false;
                        }
                    }, 2 * 1000);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_web_transfer) {
            IntentUtils.intentToChooseFileActivity(this, true);
        } else if (id == R.id.nav_settings) {
            IntentUtils.intentToSettingsActivity(this);
        } else if (id == R.id.nav_about) {
            IntentUtils.intentToAboutActivity(this);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick({R.id.btn_send, R.id.btn_receive, R.id.btn_send_big, R.id.btn_receive_big, R.id.iv_mini_avator,
            R.id.rl_device, R.id.rl_file, R.id.rl_storage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
            case R.id.btn_send_big: {
                IntentUtils.intentToChooseFileActivity(this, false);
                break;
            }
            case R.id.btn_receive:
            case R.id.btn_receive_big: {
                if (NetworkUtils.isGPRSAvailable()) {
                    showCloseGPRSDialog();
                } else {
                    IntentUtils.intentToScanSenderActivity(this);
                }
                break;
            }
            case R.id.iv_mini_avator: {
                if (mDrawerLayout != null) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            }
            case R.id.rl_file:
            case R.id.rl_storage:
            case R.id.rl_device:
                ToastUtils.showLongToast("正在开发中...");
                break;
        }
    }

    private CloseGPRSDialog mCloseGPRSDialog;

    private void showCloseGPRSDialog() {
        if (null != mCloseGPRSDialog) mCloseGPRSDialog.hide();
        if (null == mCloseGPRSDialog) {
            mCloseGPRSDialog = new CloseGPRSDialog(HomeActivity.this) {
                @Override
                public void closeGPRS() {
                    mCloseGPRSDialog.hide();
                    IntentUtils.intentToSystemSettings(HomeActivity.this);
                }

                @Override
                public void useGPRS() {
                    mCloseGPRSDialog.hide();
                    IntentUtils.intentToScanSenderActivity(HomeActivity.this);
                }
            };
        }
        mCloseGPRSDialog.show();
    }

    private int mContentHeight = 0;

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        mContentHeight = ll_main.getMeasuredHeight();
        if (t > mContentHeight / 2) {
            float sAlpha = (t - mContentHeight / 2) / (float) (mContentHeight / 2);
            ll_mini_main.setVisibility(View.VISIBLE);
            ll_main.setAlpha(1 - sAlpha);
            ll_mini_main.setAlpha(sAlpha);
            tv_title.setAlpha(0);
        } else {
            float tAlpha = t / (float) mContentHeight / 2;
            tv_title.setAlpha(1 - tAlpha);
            ll_mini_main.setVisibility(View.INVISIBLE);
            ll_mini_main.setAlpha(0);
        }
    }

    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.System.canWrite(this)) {
                    ToastUtils.showLongToast("服务开启成功!");
                } else {
                    ToastUtils.showLongToast("服务未开启");
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCloseGPRSDialog) mCloseGPRSDialog.hide();
    }
}

