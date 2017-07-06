package com.example.zero.memorandum.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
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
    private String iTempFileNameString = "recordtemp_";
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

//        生成临时目录
        iRecAudioDir = Environment.getExternalStorageDirectory();

        //获得系统当前时间，并以该时间作为文件名
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        fileName = AppData.getRecordFilePath() + "record" + formatter.format(curDate) + ".amr";
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
                    copyFile(iRecAudioFile.getPath(), fileName);//复制临时文件到指定目录
                    iRecAudioFile.delete();//删除临时文件
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

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            Log.i("---复制文件---", "复制单个文件操作出错");
            e.printStackTrace();
        }

    }

    private void stopRecord() {
        if (file != null) {
              /* 停止录音 */
            try {
                iMediaRecorder.stop();
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
                iRecAudioFile = File.createTempFile(iTempFileNameString,
                        ".amr", iRecAudioDir);
                iMediaRecorder = new MediaRecorder();
            /* 设置录音来源为MIC */
                iMediaRecorder
                        .setAudioSource(MediaRecorder.AudioSource.MIC);
                iMediaRecorder
                        .setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                iMediaRecorder
                        .setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                iMediaRecorder.setOutputFile(file
                        .getAbsolutePath());
                iMediaRecorder.prepare();
                iMediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}

