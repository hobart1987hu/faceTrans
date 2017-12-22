package org.hobart.facetrans.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import org.hobart.facetrans.GlobalConfig;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.service.ServerReceiverService;
import org.hobart.facetrans.socket.service.SocketSenderService;
import org.hobart.facetrans.ui.activity.AboutActivity;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.activity.HomeActivity;
import org.hobart.facetrans.ui.activity.ReceiveFileActivity;
import org.hobart.facetrans.ui.activity.ScanReceiverActivity;
import org.hobart.facetrans.ui.activity.ScanSenderActivity;
import org.hobart.facetrans.ui.activity.SendFileActivity;
import org.hobart.facetrans.ui.activity.SettingsActivity;
import org.hobart.facetrans.ui.activity.WebTransferActivity;

/**
 * Created by huzeyin on 2017/11/14.
 */

public class IntentUtils {

//    public static void intentMusicListActivity(Activity activity) {
//        Intent intent = new Intent(activity, MusicListActivity.class);
//        activity.startActivity(intent);
//    }

    public static void intentToSettingsActivity(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivity(intent);
    }

    public static void intentToAboutActivity(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);
    }

    public static void intentToSystemSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        activity.startActivity(intent);
    }

    public static void intentToWebTransferActivity(Activity activity) {
        Intent intent = new Intent(activity, WebTransferActivity.class);
        activity.startActivity(intent);
    }


    public static void intentToReceiveFileActivity(Activity activity) {
        Intent intent = new Intent(activity, ReceiveFileActivity.class);
        activity.startActivity(intent);
    }

    public static void intentToSendFileActivity(Activity activity) {
        Intent intent = new Intent(activity, SendFileActivity.class);
        activity.startActivity(intent);
    }

    public static void intentToScanSenderActivity(Activity activity) {
        Intent intent = new Intent(activity, ScanSenderActivity.class);
        activity.startActivity(intent);
    }


    public static void intentToScanReceiverActivity(Activity activity) {
        Intent intent = new Intent(activity, ScanReceiverActivity.class);
        activity.startActivity(intent);
    }


    public static void intentToChooseFileActivity(Activity activity, boolean isWebTransfer) {
        Intent intent = new Intent(activity, ChooseFileActivity.class);
        intent.putExtra(GlobalConfig.KEY_WEB_TRANSFER_FLAG, isWebTransfer);
        activity.startActivity(intent);
    }

    public static void intentToHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }

    public static void startServerSocketService(Context  context) {
        Intent service = new Intent(context, ServerReceiverService.class);
        service.setAction(SocketConstants.ACTION_CREATE_SERVER_SOCKET);
        context.startService(service);
    }

    public static void stopServerReceiverService(Context context) {
        Intent service = new Intent(context, ServerReceiverService.class);
        service.setAction(SocketConstants.ACTION_STOP_SERVER_SOCKET);
        context.stopService(service);
    }

    public static void startSocketSenderService(Context context, String host) {
        Intent service = new Intent(context, SocketSenderService.class);
        service.setAction(SocketConstants.ACTION_CREATE_CLIENT_SOCKET);
        service.putExtra("host", host);
        context.startService(service);
    }

    public static void stopSocketSenderService(Context context) {
        Intent service = new Intent(context, SocketSenderService.class);
        service.setAction(SocketConstants.ACTION_STOP_CLIENT_SOCKET);
        context.stopService(service);
    }

}
