package com.example.zero.flymusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by Zero on 2016/12/28.
 */

public class Player_service extends Service {
    private static Context context;
    private static Uri musicUri= null;
    private static MediaPlayer mediaPlayer = null;
    private static final String TAG = "PlayerService" ;
    public static final String ACTION = "com.example.zero.flymusic.service.PlayerService";


    public static void setMusicUri(Uri uri) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
            mediaPlayer = new MediaPlayer();
            Log.i("Player_service", "播放器实例化完成");
            try {
                mediaPlayer.setDataSource(context, uri);
                Log.i("playmusic", "设置资源成功");
                mediaPlayer.prepare();
                Log.i("playmusic", "同步成功");
            } catch (IOException e) {
                Log.w("playmusic", "获取资源失败" + e.toString());
            }
        }


    @Override
    public void onCreate() {
            context = getApplicationContext();
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                Log.i("Player_service", "播放器实例化完成");
                try {
                    mediaPlayer.setDataSource(context, musicUri);
                    Log.i("playmusic", "设置资源成功");
                    mediaPlayer.prepare();
                    Log.i("playmusic", "同步成功");
                } catch (Exception e) {
                    Log.w("playmusic", "获取资源失败" + e.toString());
                }
            }
            super.onCreate();
        }


    //    停止播放并释放资源
    private void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void play() {
        if (mediaPlayer!=null&&!mediaPlayer.isPlaying()) {
            Log.i("play","尝试播放");
            mediaPlayer.start();
        }
    }

//    public static boolean getIsPlaying(){
//        return mediaPlayer.isPlaying();
//    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Player_service","onStartCommand");
//        stop();
//        mediaPlayer.reset();
//        Log.i("playmusic","重置播放器");
//        if(musicUri.toString()!=intent.getStringExtra("uri")){
//            musicUri = Uri.parse(intent.getStringExtra("uri"));
//            Log.i("Player_service","获取Uri"+musicUri.toString());
//        }
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            Log.i("Player_service","获取bendle成功");
            if (bundle != null) {
                int index = bundle.getInt("index");
                Log.i("Player_service","获取index成功");
                if(mediaPlayer!=null) {
                    switch (index) {
                        case 1:
                            if (mediaPlayer.isPlaying()) {
                                Log.i(TAG, "播放→暂停");
                                pause();
                                break;
                            } else {
                                Log.i(TAG, "暂停→播放");
                                play();
                                break;
                            }
                        case 2:
                            play();
                            break;
//                    case 3:
//                        pause();
//                        break;
                    }
                }
                else {
                    Toast.makeText(context,"请在本地音乐中选择歌曲！",Toast.LENGTH_LONG).show();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
