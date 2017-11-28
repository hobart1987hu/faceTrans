package org.hobart.facetrans.util;

import android.app.Activity;
import android.content.Intent;

import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.socket.service.ServerReceiverService;
import org.hobart.facetrans.socket.service.SocketSenderService;
import org.hobart.facetrans.ui.activity.ChooseFileActivity;
import org.hobart.facetrans.ui.activity.HomeActivity;
import org.hobart.facetrans.ui.activity.ReceiveFileActivity;
import org.hobart.facetrans.ui.activity.ScanReceiverActivity;
import org.hobart.facetrans.ui.activity.ScanSenderActivity;
import org.hobart.facetrans.ui.activity.SendFileActivity;

/**
 * Created by huzeyin on 2017/11/14.
 */

public class IntentUtils {

//    public static void intentMusicListActivity(Activity activity) {
//        Intent intent = new Intent(activity, MusicListActivity.class);
//        activity.startActivity(intent);
//    }

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


    public static void intentToChooseFileActivity(Activity activity) {
        Intent intent = new Intent(activity, ChooseFileActivity.class);
        activity.startActivity(intent);
    }

    public static void intentToHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }

    public static void startServerSocketService(Activity activity) {
        Intent service = new Intent(activity, ServerReceiverService.class);
        service.setAction(SocketConstants.ACTION_CREATE_SERVER_SOCKET);
        activity.startService(service);
    }

    public static void stopServerReceiverService(Activity activity) {
        Intent service = new Intent(activity, ServerReceiverService.class);
        service.setAction(SocketConstants.ACTION_STOP_SERVER_SOCKET);
        activity.stopService(service);
    }

    public static void startClientSocketService(Activity activity, String host) {
        Intent service = new Intent(activity, SocketSenderService.class);
        service.setAction(SocketConstants.ACTION_CREATE_CLIENT_SOCKET);
        service.putExtra("host", host);
        activity.startService(service);
    }

    public static void stopClientSocketService(Activity activity) {
        Intent service = new Intent(activity, SocketSenderService.class);
        service.setAction(SocketConstants.ACTION_STOP_CLIENT_SOCKET);
        activity.stopService(service);
    }

}
