package com.thanh.dien.simplecallrecorder;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dien.simplecallrecorder.R;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{

    Switch aSwitch;
    //ArrayList<String> paths;
    //PlayListAdapter adapter;
    //ArrayList<PlayerAdapter> mPlayer = new ArrayList<PlayerAdapter>();
    //private ListView lv;
    private TextView textList, textView, textView2;
    private ImageView imvrefresh, imageView, imageViewClose;
    //int global_position;
    SharedPreferences sharedPreferences;
    SeekBar sbar;
    ImageView pla;
    Uri u;
    MediaPlayer mp;
    Thread updateSeekBar;
    private double thoiGianBatDau = 0;
    private double thoiGianKetThuc = 0;
    private Handler myHandler = new Handler();
    int loadSeekBar;
    private AdView mAdView;

    private RecyclerView recyclerView;
    private SongsAdapter songsAdapter;
    SongsManager songsManager;
    String MEDIA_PATH = Environment.getExternalStorageDirectory() + "";
    ArrayList<HashMap<String, String>> songList = new ArrayList<>();


    Cursor c;


    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initComponents();
        onOff();
        reFresh();
        //scrollList();
        //longClick();
        //removeAll();
        //playList();
        //initList();

        songsManager = new SongsManager();
        songList = songsManager.getPlayList(MEDIA_PATH);
        songsAdapter = new SongsAdapter(songList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(songsAdapter);


        /*String cols[] = {MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA
        };

        c = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cols, null, null, null);*/

        /*mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                super.onAdClicked();
                finish();
            }
        });*/

        boolean value = false;

        sharedPreferences = getSharedPreferences("isChecked", 0);
        value = sharedPreferences.getBoolean("isChecked", value);
        aSwitch.setChecked(value);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context !=
                null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showInterstitial(){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed(){
        showInterstitial();
    }

    public void updateProgressBar() {
        myHandler.postDelayed(updateSeekBar, 100);
    }

    private Runnable CapNhatThoiGian = new Runnable() {
        @Override
        public void run() {
            thoiGianBatDau = mp.getCurrentPosition();
            long phutBatDau = TimeUnit.MILLISECONDS.toMinutes((long)thoiGianBatDau);
            long giayBatDau = TimeUnit.MILLISECONDS.toSeconds((long)thoiGianBatDau)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)thoiGianBatDau));

            textView.setText(String.format("%d:%d", phutBatDau, giayBatDau));
            myHandler.postDelayed(this, 100);
            sbar.setProgress((int)thoiGianBatDau);
        }
    };

    public void ChuyenThoiGian(){
        long phutBatDau = TimeUnit.MILLISECONDS.toMinutes((long)thoiGianBatDau);
        long giayBatDau = TimeUnit.MILLISECONDS.toSeconds((long)thoiGianBatDau)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)thoiGianBatDau));

        textView.setText(String.format("%d:%d", phutBatDau, giayBatDau));

        long phutKetThuc = TimeUnit.MILLISECONDS.toMinutes((long)thoiGianKetThuc);
        long giayKetThuc = TimeUnit.MILLISECONDS.toSeconds((long)thoiGianKetThuc)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)thoiGianKetThuc));

        textView2.setText(String.format("%d:%d", phutKetThuc, giayKetThuc));
    }

    private void playList() {

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setCancelable(false);
                View mView = getLayoutInflater().inflate(R.layout.play_dialog, null);
                mBuilder.setView(mView);
                sbar = (SeekBar)mView.findViewById(R.id.seekbar_play);
                pla = (ImageView)mView.findViewById(R.id.iv_play);
                textView = (TextView)mView.findViewById(R.id.tv_startTime);
                textView2 = (TextView)mView.findViewById(R.id.tv_endTime);
                imageViewClose = (ImageView)mView.findViewById(R.id.iv_close);

                mp = MediaPlayer.create(getApplicationContext(), u);

                pla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mp.isPlaying()) {
                            if (mp != null) {
                                mp.pause();
                                pla.setImageResource(R.drawable.icplay);
                            }
                        } else {
                            if (mp != null) {
                                mp.start();
                                pla.setImageResource(R.drawable.icstop);
                            }
                        }


                        thoiGianBatDau = mp.getCurrentPosition();
                        thoiGianKetThuc = mp.getDuration();

                        if (loadSeekBar == 0) {
                            sbar.setMax((int)thoiGianKetThuc);
                        }
                        sbar.setProgress((int)thoiGianBatDau);
                        ChuyenThoiGian();
                        myHandler.postDelayed(CapNhatThoiGian, 100);

                    }

                });



                sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    boolean userTouch;

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        userTouch = false;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        userTouch = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        if(mp.isPlaying() && userTouch)
                            mp.seekTo(progress);
                    }
                });


                final AlertDialog dialog = mBuilder.create();

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                        mp.stop();
                    }
                });


                dialog.show();
            }
        });
    }

            /*private void removeAll() {
                textList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog2 = new AlertDialog.Builder(MainActivity.this);
                        dialog2.setCancelable(false);
                        dialog2.setTitle("Dialog on Android");
                        dialog2.setMessage("Are you sure you want to delete this entry?");
                        dialog2.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Action for "Delete".
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SimpleCallRecorder";
                                File file = new File(path);
                                String[] myFiles;
                                myFiles = file.list();
                                for (int i = 0; i < myFiles.length; i++) {
                                    File myFile = new File(file, myFiles[i]);
                                    myFile.delete();
                                }

                                // 1. adapter.clearData();
                                adapter.notifyDataSetChanged();
                            }
                        })
                                .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.cancel();
                                    }
                                });

                        final AlertDialog alert2 = dialog2.create();
                        alert2.show();
                    }
                });
            }*/

           /* private void longClick() {
                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        //adapter.remove(o);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setCancelable(false);
                        dialog.setTitle("Dialog on Android");
                        dialog.setMessage("Are you sure you want to delete this entry?");
                        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SimpleCallRecorder";
                                File file = new File(path);

                                String[] children = file.list();
                                new File(file, children[i]).delete();
                                // 2. adapter.remove(adapter.getItem(i));

                                adapter.notifyDataSetChanged();
                            }
                        }).setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        final AlertDialog alert = dialog.create();
                        alert.show();

                        return true;
                    }
                });
            }*/

           /* private void scrollList() {
                lv.setOnScrollListener(new OnScrollListener() {

                    @Override
                    public void onScroll(AbsListView view,
                                         int firstVisibleItem, int visibleItemCount,
                                         int totalItemCount) {

                        final int lastItem = firstVisibleItem + visibleItemCount;
                        if (lastItem == totalItemCount) {

                        }
                    }

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }
                });
            }*/

            private void reFresh() {
                imvrefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
            }

            private void onOff() {
                aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        try {
                            if (isChecked) {
                                PackageManager pm = MainActivity.this.getPackageManager();
                                ComponentName componentName = new ComponentName(MainActivity.this, MyReceiver.class);
                                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                        PackageManager.DONT_KILL_APP);
                                Toast.makeText(getApplicationContext(), "activated", Toast.LENGTH_SHORT).show();
                                sharedPreferences.edit().putBoolean("isChecked", true).apply();

                            } else {

                                PackageManager pm = MainActivity.this.getPackageManager();
                                ComponentName componentName = new ComponentName(MainActivity.this, MyReceiver.class);
                                pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                        PackageManager.DONT_KILL_APP);
                                Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_SHORT).show();
                                sharedPreferences.edit().putBoolean("isChecked", false).apply();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

            private void initViews() {
                //lv = (ListView)findViewById(R.id.lvList);
                recyclerView = (RecyclerView)findViewById(R.id.rcv_list);
                aSwitch = (Switch) findViewById(R.id.sw);
                imvrefresh = (ImageView) findViewById(R.id.imv_refresh);
                textList = (TextView) findViewById(R.id.tv_list);
                //bPlay = (Button)findViewById(R.id.btnPlay);
            }

            private void initComponents() {
                /*initList();
                adapter = new PlayListAdapter(this, paths);
                lv.setAdapter(adapter);*/
                //adapter = new PlayListAdapter(App.getContext(), paths);
                //lv.setAdapter(adapter);

            }

            /*private void initList() {
                paths = new ArrayList<>();

                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SimpleCallRecorder";
                File file = new File(path);
                File[] files = file.listFiles();
                try {
                    for (int i = 0; i < files.length; i++) {
                        String s = files[i].getName();
                        if (s.endsWith(".3gpp")) {
                            paths.add(files[i].getName());

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }*/

           /*public ArrayList<File> findSongs(File root)
           {

                   ArrayList<File> a1 = new ArrayList<File>();
                   File[] files = root.listFiles();
                   for (File singleFile : files) {
                       if (singleFile.isDirectory() && !singleFile.isHidden()) {
                           a1.addAll(findSongs(singleFile));
                       } else {
                           if (singleFile.getName().endsWith(".3gpp")) {
                               a1.add(singleFile);
                           }
                       }
                   }
                   return a1;

           }*/


}
