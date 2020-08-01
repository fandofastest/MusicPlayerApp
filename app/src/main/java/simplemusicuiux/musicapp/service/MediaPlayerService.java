package simplemusicuiux.musicapp.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Handler;
import android.os.IBinder;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import simplemusicuiux.musicapp.PlayerFragment;

import static simplemusicuiux.musicapp.MainActivity.LOOPINGSTATUS;
import static simplemusicuiux.musicapp.MainActivity.PLAYERSTATUS;
import static simplemusicuiux.musicapp.MainActivity.currentduraiton;
import static simplemusicuiux.musicapp.MainActivity.totalduration;

public class MediaPlayerService extends Service {

    //player
    private MediaPlayer mp = new MediaPlayer();


    //receiver

    @Override
    public void onCreate() {
        super.onCreate();


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");

                if (status.equals("pause")){
                    mp.pause();
                }
                else if (status.equals("resume")){
                    mp.start();

                }

                else  if (status.equals("seek")){
                    int seek = intent.getIntExtra("seektime",0);

                    mp.pause();
                    mp.seekTo(seek);
                    mp.start();

                }
                else if (status.equals("stopmusic")){
                    mp.release();
                }
                else if (status.equals("getduration")){
                    totalduration=mp.getDuration();
                    currentduraiton=mp.getCurrentPosition();
                }

            }
        }, new IntentFilter("fando"));

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }







    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        try {



            String mediaurl = intent.getStringExtra("mediaurl");



            mp.stop();
            mp.reset();
            mp.release();



            Uri myUri = Uri.parse(mediaurl);
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mp.prepareAsync(); //don't use prepareAsync for mp3 playback

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    return true;
                }
            });


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp1) {
                    System.out.println("error looping");

                    Intent intent = new Intent("fando");
                    intent.putExtra("status", "stoping");
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                }



            });



            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPrepared(MediaPlayer mplayer) {


                    if (mplayer.isPlaying()) {
                        mp.pause();

                    } else {
                        mp.start();
                        PLAYERSTATUS="PLAYING";
                        Intent intent = new Intent("fando");
                        intent.putExtra("status", "playing");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

//                        final Handler handler = new Handler();
//                        final int delay = 100; //milliseconds


//                        if (mp.isPlaying()){
//                            handler.postDelayed(new Runnable(){
//                                public void run(){
//                                    //do something
//                                    currentduraiton=mp.getCurrentPosition();
//                                    totalduration=mp.getDuration();
//                                    handler.postDelayed(this, delay);
//                                }
//                            }, delay);
//                        }



                    }

                }


            });





            mp.prepareAsync();


        }
        catch (Exception e){
            System.out.println(e);
        }



        return START_STICKY;
    }









}
