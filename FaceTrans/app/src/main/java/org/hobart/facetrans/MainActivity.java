package org.hobart.facetrans;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.hobart.facetrans.ui.socket.ClientScanQRActivity;
import org.hobart.facetrans.ui.socket.ServerCreateQRActivity;
import org.hobart.facetrans.util.AndroidUtils;

/***
 *
 * 业务逻辑：
 *
 * 1、首页显示文件列表
 *
 * 2、点击需要发送的文件
 *
 * 3、选择好文件之后，跳转到网络创建界面，如果网络创建成功，进行显示，失败，就不要显示
 *
 *
 * */


public class MainActivity extends AppCompatActivity {

    private static final String LOG_PREFIX = "MainActivity->";

    private boolean isWriteSettingsGranted = true;

    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23 && !Settings.System.canWrite(MainActivity.this)) {
            isWriteSettingsGranted = false;
            AndroidUtils.requestWriteSettings(MainActivity.this, REQUEST_CODE_WRITE_SETTINGS);
        }

        findViewById(R.id.btnReceive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWriteSettingsGranted) {
                    startActivity(new Intent(MainActivity.this, ClientScanQRActivity.class));
                    finish();
                }
            }
        });
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWriteSettingsGranted) {
                    startActivity(new Intent(MainActivity.this, ServerCreateQRActivity.class));
                    finish();
                }
            }
        });
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
