package com.thanh.dien.simplecallrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Created by DIEN on 11/22/2017.
 */

public class Recorder extends Service {

    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;
    private MediaRecorder mRecorder;
    int commandType;
    private String mFileName = null;
    private String mFilePath = null;
    private long mStartingTimeMillis = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        stopService();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            commandType = intent.getIntExtra("commandType", 0);
            if (commandType != 0)
                if (commandType == STATE_CALL_START){
                    try {
                        startRecorder(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(commandType==STATE_CALL_END){
            try {
                stopRecorder();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
            return super.onStartCommand(intent, flags, startId);
            //return START_STICKY;

    }

    private void stopService() {
        this.stopSelf();
    }


    private void startRecorder(Intent intent){

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(getFilePath());
            //mediaRecorder.setOutputFile(outputFile + "/d" +myDate+".3gpp");
            //mediaRecorder.setAudioSamplingRate(44100);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecorder(){
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getFilePath(){

        String filePath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filePath, "SimpleCallRecorder");

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath()+"/"+System.currentTimeMillis()+".3gpp");

    }

}
