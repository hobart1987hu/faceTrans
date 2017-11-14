package org.hobart.facetrans.util;

import android.app.Activity;
import android.content.Intent;

import org.hobart.facetrans.socket.ServerSocketService;
import org.hobart.facetrans.socket.SocketClientService;
import org.hobart.facetrans.socket.SocketConstants;
import org.hobart.facetrans.ui.HomeActivity;

/**
 * Created by huzeyin on 2017/11/14.
 */

public class IntentUtils {

    public static void intentToHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        activity.startActivity(intent);
    }

    public static void startServerSocketService(Activity activity) {
        Intent service = new Intent(activity, ServerSocketService.class);
        service.setAction(SocketConstants.ACTION_CREATE_SERVER_SOCKET);
        activity.startService(service);
    }

    public static void stopServerSocketService(Activity activity) {
        Intent service = new Intent(activity, ServerSocketService.class);
        service.setAction(SocketConstants.ACTION_STOP_SERVER_SOCKET);
        activity.stopService(service);
    }

    public static void startClientSocketService(Activity activity, String host) {
        Intent service = new Intent(activity, SocketClientService.class);
        service.setAction(SocketConstants.ACTION_CREATE_CLIENT_SOCKET);
        service.putExtra("host", host);
        activity.startService(service);
    }

    public static void stopClientSocketService(Activity activity) {
        Intent service = new Intent(activity, SocketClientService.class);
        service.setAction(SocketConstants.ACTION_STOP_CLIENT_SOCKET);
        activity.stopService(service);
    }

}
