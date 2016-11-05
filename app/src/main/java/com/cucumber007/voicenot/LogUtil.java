package com.cucumber007.voicenot;

import android.util.Log;

public class LogUtil {

    private static String TAG = "cutag";
    private static boolean DISABLE_LOGS_IN_PRODUCTION = true;

    public static void logError(Throwable throwable) {
        throwable.printStackTrace();
        logcat(throwable.getMessage());
    }

    public static void logError(Throwable throwable, String message) {
        throwable.printStackTrace();
        logcat(throwable.getMessage());
    }


    @SafeVarargs
    public static <T> void logDebug(Character delim, T... vars) {
        String log = "";
        for (int i = 0; i < vars.length; i++) {
            log += vars[i].toString() + delim;
        }
        logcat(log);
    }

    @SafeVarargs
    public static <T> void logDebug(String title, T... vars) {
        String log = title+ " : ";
        for (int i = 0; i < vars.length; i++) {
            log += vars[i].toString() + " ";
        }
        logcat(log);
    }

    @SafeVarargs
    public static <T> void logDebug(boolean condition, String title, T... vars) {
        if (condition) {
            String log = title + " : ";
            for (int i = 0; i < vars.length; i++) {
                log += vars[i].toString() + " ";
            }
            logcat(log);
        }
    }



    public static void logcat(String message) {
        Log.d(TAG, message);
    }

    public static void logcat(String tag, String message) {
        Log.d(tag, message);
    }


    public static void setDisableLogsInProduction(boolean disableLogsInProduction) {
        DISABLE_LOGS_IN_PRODUCTION = disableLogsInProduction;
    }
}
