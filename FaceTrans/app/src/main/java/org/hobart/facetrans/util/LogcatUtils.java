package org.hobart.facetrans.util;

import android.text.TextUtils;
import android.util.Log;

import org.hobart.facetrans.GlobalConfig;

/**
 * Created by huzeyin on 2017/11/7.
 */

public class LogcatUtils {

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
            String tag = getDefaultTag(stackTraceElement);
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void v(String tag, String message) {
        if (IS_DEBUG && LEVEL <= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.v(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(String message) {
        if (IS_DEBUG && LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.d(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void d(String tag, String message) {
        if (IS_DEBUG && LEVEL <= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.d(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(String message) {
        if (IS_DEBUG && LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.i(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void i(String tag, String message) {
        if (IS_DEBUG && LEVEL <= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.i(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(String message) {
        if (IS_DEBUG && LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getDefaultTag(stackTraceElement);
            Log.w(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void w(String tag, String message) {
        if (IS_DEBUG && LEVEL <= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.w(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    public static void e(String tag, String message) {
        if (IS_DEBUG && LEVEL <= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            if (TextUtils.isEmpty(tag)) {
                tag = getDefaultTag(stackTraceElement);
            }
            Log.e(tag, getLogInfo(stackTraceElement) + message);
        }
    }

    private static String getDefaultTag(StackTraceElement stackTraceElement) {
        String fileName = stackTraceElement.getFileName();
        String stringArray[] = fileName.split("\\.");
        String tag = stringArray[0];
        return tag;
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
