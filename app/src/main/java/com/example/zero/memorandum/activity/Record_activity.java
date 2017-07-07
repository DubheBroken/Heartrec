package com.example.zero.memorandum.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zero.memorandum.AppData;
import com.example.zero.memorandum.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Developer on 2017/7/6.
 */

public class Record_activity extends Activity implements View.OnClickListener {

    //    注册控件
    private TextView textRecord;
    private ImageButton btnRecord;

    File file;
    private File iRecAudioFile;
    private File iRecAudioDir;
    private String fileName = null;

    private MediaRecorder iMediaRecorder;
    private boolean isRecording = false, isPlaying = false; //标记

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_layout);

        initView();
        AppData.setFinalPage(3);

//        请求权限
        ActivityCompat.requestPermissions(Record_activity.this, new String[]{Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

//        创建录音保存目录
        file = new File(AppData.getRecordFilePath());
        if (!file.exists()) {
            file.mkdirs();
        }

        //获得系统当前时间，并以该时间作为文件名
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        fileName = AppData.getRecordFilePath() + "record" + formatter.format(curDate) + ".aac";
        file = new File(fileName);
    }

    private void initView() {

//        实例化控件
        textRecord = (TextView) findViewById(R.id.text_record);
        btnRecord = (ImageButton) findViewById(R.id.btn_record);

        btnRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record:
                if (isRecording) {
//                    结束录制
                    textRecord.setText("录制完成");
                    btnRecord.setImageResource(R.mipmap.btn_recorder);
                    isRecording = false;
                    stopRecord();
                    finish();
                } else {
//                    开始录制
                    isRecording = true;
                    textRecord.setText("点击按钮停止录制");
                    btnRecord.setImageResource(R.mipmap.btn_stop);
                    startRecord();
                }
                break;
        }
    }


    private void stopRecord() {
        if (file != null) {
              /* 停止录音 */
            try {
                iMediaRecorder.stop();
                iMediaRecorder.reset();
                iMediaRecorder.release();
                iMediaRecorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }
    private void startRecord() {
         /* 创建录音文件 */
        try {
            iMediaRecorder = new MediaRecorder();
            /* 设置录音来源为MIC */
            iMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            iMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            iMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            iMediaRecorder.setOutputFile(fileName);

            iMediaRecorder.prepare();
            iMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

