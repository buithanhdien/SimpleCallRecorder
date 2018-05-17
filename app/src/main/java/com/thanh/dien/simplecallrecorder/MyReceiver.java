package com.thanh.dien.simplecallrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by DIEN on 11/22/2017.
 */

public class MyReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static String savedNumber;
    public static final int STATE_CALL_START = 1;
    public static final int STATE_CALL_END = 2;
    private Intent myIntent;

    @Override
    public void onReceive(final Context context, Intent intent) {

        try {

            if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");

            }
            else{
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                int state = 0;
                if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){

                    state = TelephonyManager.CALL_STATE_IDLE;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                }
                else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number);

            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void onCallStateChanged(Context context, int state, String number) {

        try{

            if(lastState == state){
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(context, "Ringing" , Toast.LENGTH_SHORT).show();

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Toast.makeText(context, "Star Recorder", Toast.LENGTH_SHORT).show();
                    myIntent = new Intent(context, Recorder.class);
                    myIntent.putExtra("commandType", STATE_CALL_START);
                    context.startService(myIntent);
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    Toast.makeText(context, "Stop Recorder", Toast.LENGTH_SHORT).show();
                    myIntent = new Intent(context, Recorder.class);
                    myIntent.putExtra("commandType", STATE_CALL_END);
                    context.startService(myIntent);
                    break;

            }
            lastState = state;

        }catch(Exception e){
            e.printStackTrace();
        }
    }


}

