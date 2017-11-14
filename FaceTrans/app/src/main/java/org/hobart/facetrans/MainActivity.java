package org.hobart.facetrans;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.LogcatUtils;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private boolean isWriteSettingsGranted = true;

    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RxPermissions(this).request(Manifest.permission.WRITE_SETTINGS, Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (!aBoolean) {
                            LogcatUtils.d(TAG, "permissions 拒绝!");
                        } else {
                            //
                            LogcatUtils.d(TAG, "permissions 允许!");
                        }
                    }
                });

        findViewById(R.id.btnReceive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWriteSettingsGranted) {
                    //TODO:
                }
            }
        });
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWriteSettingsGranted) {
                    startActivity(new Intent(MainActivity.this, FanTransServerActivity.class));
                    finish();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(this)) {
            isWriteSettingsGranted = false;
            AndroidUtils.requestWriteSettings(this, REQUEST_CODE_WRITE_SETTINGS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.System.canWrite(this)) {
                    isWriteSettingsGranted = true;
                } else {
                    isWriteSettingsGranted = false;
                }
            }
        }
    }
}
