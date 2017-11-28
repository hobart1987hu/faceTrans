//package org.hobart.facetrans.ui.activity;
//
//import android.Manifest;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.provider.Settings;
//import android.support.annotation.Nullable;
//import android.widget.TextView;
//
//import com.tbruyelle.rxpermissions.RxPermissions;
//
//import org.hobart.facetrans.FTType;
//import org.hobart.facetrans.R;
//import org.hobart.facetrans.model.Image;
//import org.hobart.facetrans.model.Music;
//import org.hobart.facetrans.model.Video;
//import org.hobart.facetrans.task.FTTaskCallback;
//import org.hobart.facetrans.task.impl.ImageAsyncTask;
//import org.hobart.facetrans.task.impl.MusicAsyncTask;
//import org.hobart.facetrans.task.impl.VideoAsyncTask;
//import org.hobart.facetrans.ui.activity.base.BaseActivity;
//import org.hobart.facetrans.util.AndroidUtils;
//import org.hobart.facetrans.util.IntentUtils;
//import org.hobart.facetrans.util.LogcatUtils;
//import org.hobart.facetrans.util.ToastUtils;
//
//import java.util.List;
//
//import rx.functions.Action1;
//
///**
// * Created by huzeyin on 2017/11/14.
// */
//
//public class SplashActivity extends BaseActivity {
//
//    private TextView tv_info;
//
//    private boolean isWriteSettingsGranted = true;
//
//    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_splash);
//        tv_info = (TextView) findViewById(R.id.tv_info);
//
//        //tood;
//        new ImageAsyncTask(new FTTaskCallback<List<Image>>() {
//            @Override
//            public void onPreExecute() {
//
//            }
//
//            @Override
//            public void onExecute() {
//
//            }
//
//            @Override
//            public void onCancelled() {
//
//            }
//
//            @Override
//            public void onFinished(List<Image> musics) {
//                for (Image music : musics) {
//                    LogcatUtils.d("SplashActivity->" + music.toString());
//                }
//            }
//        }).execute();
//
//        new RxPermissions(this)
//                .request(Manifest.permission.READ_EXTERNAL_STORAGE
//                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .subscribe(new Action1<Boolean>() {
//                    @Override
//                    public void call(Boolean aBoolean) {
//                        if (!aBoolean) {
//                            finish();
//                            return;
//                        }
//                        if (Build.VERSION.SDK_INT >= 23) {
//                            if (!Settings.System.canWrite(SplashActivity.this)) {
//                                isWriteSettingsGranted = false;
//                                tv_info.setText("欢迎使用面传app\n检测到您需要开启允许修改系统设置服务");
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        AndroidUtils.requestWriteSettings(SplashActivity.this, REQUEST_CODE_WRITE_SETTINGS);
//                                    }
//                                }, 1000);
//                            } else {
//                                IntentUtils.intentToHomeActivity(SplashActivity.this);
//                                finish();
//                            }
//                        }
//                    }
//                });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (Settings.System.canWrite(this)) {
//                    isWriteSettingsGranted = true;
//                    ToastUtils.showLongToast("服务开启成功!");
//                    IntentUtils.intentToHomeActivity(SplashActivity.this);
//                    finish();
//                } else {
//                    isWriteSettingsGranted = false;
//                    ToastUtils.showLongToast("服务未开启！");
//                }
//            }
//        }
//    }
//}
