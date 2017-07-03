package com.example.zero.memorandum;

import android.app.Application;

/**
 * Created by Developer on 2017/7/3.
 */

public class AppData extends Application {

    private static String imageFilePath = "sdcard/MyMemorandum/";

    public static String getImageFilePath() {
        return imageFilePath;
    }

    public static void setImageFilePath(String imageFilePath) {
        AppData.imageFilePath = imageFilePath;
    }
}
