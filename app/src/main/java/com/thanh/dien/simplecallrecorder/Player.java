package com.thanh.dien.simplecallrecorder;

import android.app.Activity;
import android.os.Bundle;

import com.example.dien.simplecallrecorder.R;

public class Player extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_dialog);
    }
}
