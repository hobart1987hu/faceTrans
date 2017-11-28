package org.hobart.facetrans.util;

import android.util.Log;

import org.hobart.facetrans.GlobalConfig;

/**
 * Created by huzeyin on 2017/11/7.ßß
 */

public class LogcatUtils {

    private static final String TAG = "FaceTrans";

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int LEVEL = VERBOSE;

    public static final String SEPARATOR = ",";

    private static boolean IS_DEBUG = GlobalConfig.DEBUG;

    public static void v(String message) {
        if (IS_DEBUG && LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.v(TAG, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(String message) {
        if (IS_DEBUG && LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.d(TAG, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(String message) {
        if (IS_DEBUG && LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.i(TAG, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(String message) {
        if (IS_DEBUG && LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.w(TAG, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void e(String message) {
        if (IS_DEBUG && LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            Log.e(TAG, getLogInfo(stackTraceElement) + message);
        }
    }

    private static String getLogInfo(StackTraceElement stackTraceElement) {
        StringBuilder logInfoStringBuilder = new StringBuilder();
        int lineNumber = stackTraceElement.getLineNumber();
        logInfoStringBuilder.append("[ ");
        logInfoStringBuilder.append("lineNumber=" + lineNumber);
        logInfoStringBuilder.append(" ] ");
        return logInfoStringBuilder.toString();
    }
}
