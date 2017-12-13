package org.hobart.facetrans.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.hobart.facetrans.FTType;
import org.hobart.facetrans.FaceTransApplication;
import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.R;
import org.hobart.facetrans.event.ApCreateEvent;
import org.hobart.facetrans.event.FTFilesChangedEvent;
import org.hobart.facetrans.http_server.ImageHttpInterceptor;
import org.hobart.facetrans.manager.FTFileManager;
import org.hobart.facetrans.http_server.AndroidHttpServer;
import org.hobart.facetrans.http_server.DownloadHttpUriInterceptor;
import org.hobart.facetrans.http_server.IndexHttpUriInterceptor;
import org.hobart.facetrans.model.FTFile;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.ui.activity.base.BaseActivity;
import org.hobart.facetrans.util.AndroidUtils;
import org.hobart.facetrans.util.ClassifyUtils;
import org.hobart.facetrans.util.FileUtils;
import org.hobart.facetrans.util.IOStreamUtils;
import org.hobart.facetrans.util.LogcatUtils;
import org.hobart.facetrans.util.ToastUtils;
import org.hobart.facetrans.wifi.ApWifiHelper;
import org.hobart.facetrans.wifi.CreateWifiAPThread;
import org.hobart.facetrans.wifi.WifiHelper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by huzeyin on 2017/12/9.
 */

public class WebTransferActivity extends BaseActivity {

    private static final String LOG_PREFIX = "WebTransferActivity->";

    private TextView tv_tip_1, tv_tip_2;
    private CreateWifiAPThread mCreateWifiAPThread;

    private AndroidHttpServer mServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_transfer);
        EventBus.getDefault().register(this);
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("网页传");
        tv_tip_1 = (TextView) findViewById(R.id.tv_tip_1);
        tv_tip_2 = (TextView) findViewById(R.id.tv_tip_2);
        createAp();
    }

    private void createAp() {
        WifiHelper.getInstance().closeWifi();
        if (null == mCreateWifiAPThread)
            mCreateWifiAPThread = new CreateWifiAPThread();
        new Thread(mCreateWifiAPThread).start();
        ApWifiHelper.getInstance().createWifiAP(GlobalConfig.AP_SSID, GlobalConfig.AP_PWD);
    }

    private boolean connectedSuccess = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiAPCreateCallBack(ApCreateEvent event) {
        if (event == null) {
            return;
        }
        switch (event.status) {
            case ApCreateEvent.SUCCESS:
                ToastUtils.showLongToast("Wi-Fi热点创建成功");
                LogcatUtils.d(LOG_PREFIX + "onWifiAPCreateCallBack onSuccess: 热点创建成功");
                if (!connectedSuccess) {
                    //界面显示 作为服务端
                    updateUI();
                    startServer();
                }
                connectedSuccess = true;
                break;
            case ApCreateEvent.FAILED:
                LogcatUtils.d(LOG_PREFIX + "onWifiAPCreateCallBack onFailed: 热点创建失败");
                ToastUtils.showLongToast("Wi-Fi热点创建失败！");
                finish();
                break;
            case ApCreateEvent.TRY_AGAIN:
                createAp();
                break;
            default:
                break;
        }
    }

    private void startServer() {
        try {
            new Thread(createServer()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Runnable createServer() throws Exception {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String hotspotIpAddr = WifiHelper.getInstance().getHotspotLocalIpAddress();
                    int count = 0;
                    while (hotspotIpAddr.equals(GlobalConfig.DEFAULT_UNKOWN_IP) && count < GlobalConfig.DEFAULT_TRY_TIME) {
                        Thread.sleep(1000);
                        hotspotIpAddr = WifiHelper.getInstance().getIpAddressFromHotspot();
                        count++;
                    }
                } catch (Exception e) {
                }
                mServer = new AndroidHttpServer(SocketConstants.WEB_SERVER_PORT);
                mServer.registerHttpUriInterceptor(new MyIndexHttpUriInterceptor());
                mServer.registerHttpUriInterceptor(new ImageHttpInterceptor());
                mServer.registerHttpUriInterceptor(new DownloadHttpUriInterceptor());
                mServer.start();
            }
        };
    }

    private void updateUI() {

        final String host = GlobalConfig.WEB_SERVER_IP;

        String normalColor = "#ff000000";
        String highlightColor = "#1467CD";
        String ssid = GlobalConfig.AP_SSID;
        String tip1 = getResources().getString(R.string.tip_web_transfer_first_tip).replace("{hotspot}", ssid);
        String[] tip1StringArray = tip1.split("\\n");
        Spanned tip1Spanned = Html.fromHtml("<font color='" + normalColor + "'>" + tip1StringArray[0].trim() + "</font><br>"
                + "<font color='" + normalColor + "'>" + tip1StringArray[1].trim() + "</font><br>"
                + "<font color='" + highlightColor + "'>" + tip1StringArray[2].trim() + "</font>");
        tv_tip_1.setText(tip1Spanned);

        String tip2 = getResources().getString(R.string.tip_web_transfer_second_tip);
        String[] tip2StringArray = tip2.split("\\n");
        Spanned tip2Spanned = Html.fromHtml("<font color='" + normalColor + "'>" + tip2StringArray[0].trim() + "</font><br>"
                + "<font color='" + normalColor + "'>" + tip2StringArray[1].trim() + "</font><br>"
                + "<font color='" + highlightColor + "'>" + "http://" + host + ":" + SocketConstants.WEB_SERVER_PORT + "</font><br>");
        tv_tip_2.setText(tip2Spanned);
    }

    static class MyIndexHttpUriInterceptor extends IndexHttpUriInterceptor {

        private Map<Long, FTFile> sFileInfoMap = null;

        public MyIndexHttpUriInterceptor() {
            sFileInfoMap = FTFileManager.getInstance().getFTFiles();
        }

        @Override
        public String convert(String indexHtml) {
            StringBuilder allFileListInfoHtmlBuilder = new StringBuilder();
            int count = this.sFileInfoMap.size();
            indexHtml = indexHtml.replaceAll("\\{app_icon\\}", GlobalConfig.WEB_TRANSFER_APP_ICON_IMAGE_PREFIX);
            indexHtml = indexHtml.replaceAll("\\{app_path\\}", GlobalConfig.WEB_TRANSFER_DOWNLOAD_PREFIX + AndroidUtils.getCurrentApkPath());
            indexHtml = indexHtml.replaceAll("\\{app_name\\}", FaceTransApplication.getApp().getResources().getString(R.string.app_name));
            indexHtml = indexHtml.replaceAll("\\{file_share\\}", GlobalConfig.AP_SSID);
            indexHtml = indexHtml.replaceAll("\\{file_count\\}", String.valueOf(count));

            List<FTFile> apkInfos = ClassifyUtils.filter(this.sFileInfoMap, FTType.APK);
            List<FTFile> imageInfos = ClassifyUtils.filter(this.sFileInfoMap, FTType.IMAGE);
            List<FTFile> musicInfos = ClassifyUtils.filter(this.sFileInfoMap, FTType.MUSIC);
            List<FTFile> videoInfos = ClassifyUtils.filter(this.sFileInfoMap, FTType.VIDEO);

            try {
                String apkInfosHtml = getClassifyFileInfoListHtml(apkInfos, FTType.APK);
                String imageInfosHtml = getClassifyFileInfoListHtml(imageInfos, FTType.IMAGE);
                String musicInfosHtml = getClassifyFileInfoListHtml(musicInfos, FTType.MUSIC);
                String videoInfosHtml = getClassifyFileInfoListHtml(videoInfos, FTType.VIDEO);

                allFileListInfoHtmlBuilder.append(apkInfosHtml);
                allFileListInfoHtmlBuilder.append(imageInfosHtml);
                allFileListInfoHtmlBuilder.append(musicInfosHtml);
                allFileListInfoHtmlBuilder.append(videoInfosHtml);
                indexHtml = indexHtml.replaceAll("\\{file_list_template\\}", allFileListInfoHtmlBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return indexHtml;
        }

        private String getFileInfoListHtml(List<FTFile> fileInfos) throws IOException {
            StringBuilder sb = new StringBuilder();
            for (FTFile fileInfo : fileInfos) {
                String fileInfoHtml = IOStreamUtils.inputStreamToString(FaceTransApplication.getApp().getAssets().open(GlobalConfig.NAME_FILE_TEMPLATE));
                if (fileInfo.getFileType() == FTType.APK) {
                    fileInfoHtml = fileInfoHtml.replaceAll("\\{file_avatar\\}", GlobalConfig.WEB_TRANSFER_APK_IMAGE_PREFIX + fileInfo.getId());
                } else if (fileInfo.getFileType() == FTType.MUSIC) {
                    fileInfoHtml = fileInfoHtml.replaceAll("\\{file_avatar\\}", GlobalConfig.WEB_TRANSFER_MUSIC_IMAGE_PREFIX + fileInfo.getId());
                } else if (fileInfo.getFileType() == FTType.VIDEO) {
                    fileInfoHtml = fileInfoHtml.replaceAll("\\{file_avatar\\}", GlobalConfig.WEB_TRANSFER_VIDEO_IMAGE_PREFIX + fileInfo.getId());
                } else {
                    fileInfoHtml = fileInfoHtml.replaceAll("\\{file_avatar\\}", GlobalConfig.WEB_TRANSFER_IMAGE_PREFIX + fileInfo.getFilePath());
                }
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_name\\}", FileUtils.getFileName(fileInfo.getFilePath()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_size\\}", FileUtils.getFileSize(fileInfo.getSize()));
                fileInfoHtml = fileInfoHtml.replaceAll("\\{file_path\\}", GlobalConfig.WEB_TRANSFER_DOWNLOAD_PREFIX + fileInfo.getFilePath());
                sb.append(fileInfoHtml);
            }
            return sb.toString();
        }

        private String getClassifyFileInfoListHtml(List<FTFile> fileInfos, FTType type) throws IOException {
            if (fileInfos == null || fileInfos.size() <= 0) {
                return "";
            }
            String classifyHtml = IOStreamUtils.inputStreamToString(FaceTransApplication.getApp().getAssets().open(GlobalConfig.NAME_CLASSIFY_TEMPLATE));

            String className = "";

            if (type == FTType.APK) {
                className = "应用";
            } else if (type == FTType.MUSIC) {
                className = "音乐";
            } else if (type == FTType.IMAGE) {
                className = "图片";
            } else if (type == FTType.VIDEO) {
                className = "视频";
            }
            classifyHtml = classifyHtml.replaceAll("\\{class_name\\}", className);
            classifyHtml = classifyHtml.replaceAll("\\{class_count\\}", String.valueOf(fileInfos.size()));
            classifyHtml = classifyHtml.replaceAll("\\{file_list\\}", getFileInfoListHtml(fileInfos));

            return classifyHtml;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FTFileManager.getInstance().clear();
        EventBus.getDefault().post(new FTFilesChangedEvent());
        EventBus.getDefault().unregister(this);
        if (null != mServer) mServer.stop();
        if (null != mCreateWifiAPThread) mCreateWifiAPThread.cancelDownTimer();
        //关闭热点
        ApWifiHelper.getInstance().closeWifiAp();
        //断开与当前的热点连接
        ApWifiHelper.getInstance().disableCurrentNetWork();
        //重新打开Wi-Fi
        WifiHelper.getInstance().openWifi();
    }
}
