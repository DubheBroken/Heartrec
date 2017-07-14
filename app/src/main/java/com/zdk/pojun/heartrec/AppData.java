package com.zdk.pojun.heartrec;

import android.app.Application;

/**
 * Created by Developer on 2017/7/3.
 */

public class AppData extends Application {

    private static String appHomePath = "sdcard/heartrec/";
    private static String imageFilePath = "sdcard/heartrec/paint/";
    private static String recordFilePath = "sdcard/heartrec/record/";
    private static int penColor = 0xff0000;//画笔颜色，默认红色
    private static int penSize = 9;//画笔尺寸
    private static int finalPage = 1;//最后访问的页面，默认文本

    public static int getFinalPage() {
        return finalPage;
    }

    public static void setFinalPage(int finalPage) {
        AppData.finalPage = finalPage;
    }

    public static String getAppHomePath() {
        return appHomePath;
    }

    public static void setAppHomePath(String appHomePath) {
        AppData.appHomePath = appHomePath;
    }

    public static String getImageFilePath() {
        return imageFilePath;
    }

    public static void setImageFilePath(String imageFilePath) {
        AppData.imageFilePath = imageFilePath;
    }

    public static String getRecordFilePath() {
        return recordFilePath;
    }

    public static void setRecordFilePath(String recordFilePath) {
        AppData.recordFilePath = recordFilePath;
    }

    public static int getPenColor() {
        return penColor;
    }

    public static void setPenColor(int penColor) {
        AppData.penColor = penColor;
    }

    public static int getPenSize() {
        return penSize;
    }

    public static void setPenSize(int penSize) {
        AppData.penSize = penSize;
    }
}
