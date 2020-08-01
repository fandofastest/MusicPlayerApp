package simplemusicuiux.musicapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static simplemusicuiux.musicapp.MainActivity.LOOPINGSTATUS;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment  {
    private AdView mAdView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
     private ImageView btnplay,btnstop,prev,next,loopingon,loopingoff,randomb,eqbutton,addtoplaylists,share,backarrow,playlistbutton;
     private TextView txttitle,txtartis,txtdura,txttotaldura;
      private ProgressBar progressBar;
      private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ImageView imageView,timer;
    private String title,artist,duration="0",imageurl;
    InterstitialAd mInterstitialAd;
    SweetAlertDialog pDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayerFragment() {
        // Required empty public constructor
    }
    public PlayerFragment(MediaPlayer mediaPlayer) {
        this.mediaPlayer=mediaPlayer;

        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle =getArguments();

            if (bundle.get("status").equals("online")){
                title= bundle.getString("title");
                artist=bundle.getString("artist");
                duration=bundle.getString("duration");
                imageurl=bundle.getString("imageurl");

            }
            else  {
                title= bundle.getString("title");
                artist="Local Music";
                duration=bundle.getString("duration");
                imageurl="https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcRPxNDhA-qKG1MyrP06auoEZDLw49svfJi36Q&usqp=CAU";
            }



        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_player, container, false);

        btnplay=view.findViewById(R.id.butplay);
        btnstop=view.findViewById(R.id.butpause);
        imageView=view.findViewById(R.id.image);
        txttitle=view.findViewById(R.id.txttitle);
        txtartis=view.findViewById(R.id.txtartist);
        txtdura=view.findViewById(R.id.currendura);
        txttotaldura=view.findViewById(R.id.totaldura);
        progressBar=view.findViewById(R.id.progressbar);
        seekBar=view.findViewById(R.id.seekbar);
        prev=view.findViewById(R.id.prev);
        next=view.findViewById(R.id.next);
        loopingon=view.findViewById(R.id.loopingbutton);
        loopingoff=view.findViewById(R.id.loopingbuttoff);
        eqbutton=view.findViewById(R.id.eqbutton);
        addtoplaylists=view.findViewById(R.id.addtoplaylists);
        share=view.findViewById(R.id.share);
        progressBar.setVisibility(View.VISIBLE);
        timer=view.findViewById(R.id.timer);
        backarrow=view.findViewById(R.id.backarrow);
        playlistbutton=view.findViewById(R.id.playlists);






        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = view.findViewById(R.id.adViewplayer);
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });



        if (LOOPINGSTATUS){
            loopingon.setVisibility(View.VISIBLE);
            loopingoff.setVisibility(View.GONE);
        }else{
            loopingoff.setVisibility(View.VISIBLE);
            loopingon.setVisibility(View.GONE);
        }


        MusicUtils musicUtils = new MusicUtils();

        seekBar.setProgress(0);

        seekBar.setMax(MusicUtils.MAX_PROGRESS);

        backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();


            }
        });

        playlistbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity)getActivity()).gototab(3);

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar1, int progress, boolean b) {

                if(b){

                     seekBar.setProgress(progress);
                    ((MainActivity)getActivity()).updateseekbarmp(progress);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        System.out.println(duration);
        txttotaldura.setText(musicUtils.milliSecondsToTimer(Long.parseLong(duration)));
        txttitle.setText(title);
//        txttotaldura.setText(duration);
        txtartis.setText(artist);
        Glide.with(this)
                .load(imageurl)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);


        if (MainActivity.PLAYERSTATUS.equals("PLAYING")){
            togglebuttonplay();
            hideprogressbar();

        }else  if (MainActivity.PLAYERSTATUS.equals("LOADING")){

           showprogressbar();

        }
        else  if (MainActivity.PLAYERSTATUS.equals("STOPING")){

            showprogressbar();
            togglebuttonpause();
            hideprogressbar();

        }


        eqbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("showeq");
               showinter("eq");

            }
        });


        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).pausemusic();
                btnstop.setVisibility(View.GONE);
                btnplay.setVisibility(View.VISIBLE);
            }
        });

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).resumeemusic();
                btnstop.setVisibility(View.VISIBLE);
                btnplay.setVisibility(View.GONE);

            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).next();

            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).previous();

            }
        });
        addtoplaylists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).addtoplaylits();
            }
        });

        loopingoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopingoff.setVisibility(View.GONE);
                loopingon.setVisibility(View.VISIBLE);
                LOOPINGSTATUS=true;
                Toast.makeText(getContext(),"Repeat On",Toast.LENGTH_LONG).show();

            }
        });

        loopingon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopingon.setVisibility(View.GONE);
                loopingoff.setVisibility(View.VISIBLE);
                LOOPINGSTATUS=false;
                Toast.makeText(getContext(),"Repeat Off",Toast.LENGTH_LONG).show();
            }
        });


        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showinter("timer");
                // TODO Auto-generated method stub

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });



    }

    public void  togglebuttonplay(){

        btnplay.setVisibility(View.GONE);
        btnstop.setVisibility(View.VISIBLE);

    }
    public void  togglebuttonpause(){

        btnplay.setVisibility(View.VISIBLE);
        btnstop.setVisibility(View.GONE);

    }

    public void hideprogressbar(){

        progressBar.setVisibility(View.GONE);

    }
    public void showprogressbar(){
        btnplay.setVisibility(View.GONE);
        btnstop.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }
//
//    public  void  settitle(String title,String artist,String duration){
//        txttitle.setText(title);
//        txtartis.setText(artist);
//        txttotaldura.setText(duration);
//
//
//
//
//
//    }


    void updateTimerAndSeekbar(long totalDuration, long currentDuration) {
        // Displaying Total Duration time
        MusicUtils utils = new MusicUtils();
        txttotaldura.setText(utils.milliSecondsToTimer(totalDuration));
        // Displaying time completed playing
        txtdura.setText(utils.milliSecondsToTimer(currentDuration));

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(currentDuration, totalDuration));
        seekBar.setProgress(progress);
    }

    public void settimerdialog(){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                int jammenit=selectedHour*60;
                int jamtotal=jammenit+selectedMinute;
                long jamdetik =jamtotal*60*1000;

                ((MainActivity)getActivity()).settimer(jamdetik,selectedHour + "Hours " + selectedMinute+" Minutes");
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }


    public  void showinter(final String from) {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Ads");
        pDialog.setCancelable(false);
        pDialog.show();

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId(getString(R.string.interads));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                pDialog.cancel();

                if (from.equals("timer")){
                    settimerdialog();
                }

                else {
                    ((MainActivity)getActivity()).showeq();
                }




                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                pDialog.cancel();
                if (from.equals("timer")){
                    settimerdialog();
                }

                else {
                    ((MainActivity)getActivity()).showeq();
                }


                // Code to be executed when the interstitial ad is closed.
            }
        });


    }



}
