package com.example.zero.memorandum.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Developer on 2017/7/7.
 */

public class Player extends MediaPlayer {
    private final String TAG = Player.class.getName();
    private String path;
    public boolean isPlaying = false;
    private Context context;


    public Player(Context context, String path) {
        this.path = path;
        this.context = context;
    }

    public void start() {
        if (!isPlaying) {
            try {
                isPlaying = true;
                //设置要播放的文件
                File file = new File("/mnt/" + path);
                FileInputStream fis = new FileInputStream(file);
                super.setDataSource(fis.getFD());
                super.prepare();
                //播放
                super.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isPlaying = false;
        super.stop();
        super.release();
    }

}
