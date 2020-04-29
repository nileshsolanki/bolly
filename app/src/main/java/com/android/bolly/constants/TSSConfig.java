package com.android.bolly.constants;

public class TSSConfig {

    static boolean isDownloading = false;

    public static boolean isDownloading() {
        return isDownloading;
    }

    public static void setDownloading() {
        TSSConfig.isDownloading = true;
    }

    public static void resetDownloading() {
        TSSConfig.isDownloading = false;
    }
}
