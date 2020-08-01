package simplemusicuiux.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;


import com.bullhead.equalizer.DialogEqualizerFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ModalClass.OfflineModalClass;
import ModalClass.SongModel;
import adapter.TabAdapter;
import cn.pedant.SweetAlert.SweetAlertDialog;
import guy4444.smartrate.SmartRate;
import helper.RealmHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import simplemusicuiux.musicapp.service.MediaPlayerService;

import static adapter.Topsong_RecycleView_Adapter.tredingModalClassList;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static simplemusicuiux.musicapp.LocalFragment.listoffline;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    InterstitialAd mInterstitialAd;
    SweetAlertDialog pDialog;
    Realm realm;
    RealmHelper realmHelper;
    MediaPlayer mp ;
    public static String PLAYERSTATUS="";
    public  static  int PLAYCOUNT=0;
    public  static  int ONLINESONG=0;
    public  static boolean LOOPINGSTATUS=false;
    public int currentpos;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public  static long currentduraiton,totalduration;

    private Handler mHandler = new Handler();

    List<SongModel> listrecent = new ArrayList<>();

    public static  List<SongModel> currentlistplay = new ArrayList<>();
    public static   boolean fromnext =false;

    public static SongModel currentsongmodel;
    private AdView mAdView;


    ProgressBar progressBar;
    ImageView searchimage;
    LinearLayout homeplayerlayout;
    TextView hometitle,homeartist;
    ImageView homeplay,homepause,imagehomeplayer,homenext,homeprev,loopingoff,loopingon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);
        mp = new MediaPlayer();
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        hometitle=findViewById(R.id.titlehome);
        progressBar=findViewById(R.id.progressbar);
        homeplay=findViewById(R.id.homeplay);
        homepause=findViewById(R.id.homepause);
        homeartist=findViewById(R.id.artisthome);
        homeplayerlayout=findViewById(R.id.playerhome);
        imagehomeplayer=findViewById(R.id.imagehomeplayer);
        homenext=findViewById(R.id.next);
        homeprev=findViewById(R.id.prev);
        searchimage=findViewById(R.id.search_bar);
        initrealm();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
            }
        } else {
        }
        loadbanner();


        SharedPreferences sharedPreferences = getSharedPreferences("push", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("firstTime", true)) {
            showTermServicesDialog();
        }



        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new TopsongFragment(), "TopSong");
        adapter.addFragment(new RecentFragment(), "Recent");
        adapter.addFragment(new LocalFragment(), "Local");
        adapter.addFragment(new PlaylistFragment(), "Playlist");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        searchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showsearchdialog();
            }
        });

        homeplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            resumeemusic();


            }
        });

        homepause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PLAYERSTATUS.equals("PLAYING")){
                  pausemusic();
                }

            }
        });
        homeprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        homenext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");

                if (status.equals("playing")){

                    PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");

                    System.out.println("cekdata" +fragment);
                    if (fragment!=null){
                        fragment.togglebuttonplay();
                        fragment.hideprogressbar();
                    }


                    PLAYERSTATUS="PLAYING";
                    progressBar.setVisibility(View.GONE);
                    homeplay.setVisibility(View.GONE);
                    homepause.setVisibility(View.VISIBLE);
                    mHandler.post(mUpdateTimeTask);




                }
                else if (status.equals("stoping")){


                    if (LOOPINGSTATUS){
                        repeat();
                    }
                    else {
                        next();
                    }








                }

            }
        }, new IntentFilter("fando"));




    }



    public void playmusic(int position ,List<SongModel> listplay){
        homeplayerlayout.setVisibility(View.VISIBLE);
        PLAYCOUNT++;
        if (PLAYCOUNT % 5 == 0){
            showrate();
        }

        ONLINESONG=1;

        currentpos=position;
        currentlistplay=listplay;
         final SongModel modalClass = listplay.get(position);
        currentsongmodel=modalClass;

        progressBar.setVisibility(View.VISIBLE);
        homeplay.setVisibility(View.GONE);
        homepause.setVisibility(View.GONE);
          homeplayerlayout.setVisibility(View.VISIBLE);
          homeplayerlayout.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

                  if ( PLAYERSTATUS.equals("PLAYING")){

                      FragmentManager manager=getSupportFragmentManager();
                      Bundle  bundle= new Bundle();
                      bundle.putString("status","online");
                      bundle.putString("title",modalClass.getTitle());
                      bundle.putString("artist",modalClass.getArtist());
                      bundle.putString("duration",modalClass.getDuration());
                      bundle.putString("imageurl",modalClass.getImageurl());
                      try {



                          PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
                          playerFragment.setArguments(bundle);
                          manager.beginTransaction()
                                  .remove(playerFragment)
//                    .replace(R.id.frmid, playerFragment,"playerfragment")
                                  .commit();


                          PlayerFragment playerFragment1= new PlayerFragment();
                          playerFragment1.setArguments(bundle);
                          manager.beginTransaction()
                                  .replace(R.id.frmid, playerFragment1,"playerfragment")
                                  .addToBackStack("fragment")
                                  .commit();
                      }
                      catch (Exception e){

                          PlayerFragment playerFragment= new PlayerFragment();
                          playerFragment.setArguments(bundle);
                          manager.beginTransaction()
                                  .add(R.id.frmid, playerFragment,"playerfragment")
                                  .addToBackStack("playerfragment")
                                  .commit();

                      }
                  }


              }
          });

        PLAYERSTATUS="LOADING";


          hometitle.setText(modalClass.getTitle());
          homeartist.setText(modalClass.getArtist());
          try {
              Glide
                      .with(this)
                      .load(modalClass.getImageurl())
                      .centerCrop()
                      .placeholder(R.mipmap.ic_launcher)
                      .into(imagehomeplayer);

          }

        catch (Exception e){
            System.out.println(e);

        }

          try {
              final FragmentManager manager=getSupportFragmentManager();
              Bundle  bundle= new Bundle();
              bundle.putString("status","online");
              bundle.putString("title",modalClass.getTitle());
              bundle.putString("artist",modalClass.getArtist());
              bundle.putString("duration",modalClass.getDuration());
              bundle.putString("imageurl",modalClass.getImageurl());



              PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
              playerFragment.setArguments(bundle);
              manager.beginTransaction()
                      .remove(playerFragment)
//                    .replace(R.id.frmid, playerFragment,"playerfragment")
                      .commit();


              PlayerFragment playerFragment1= new PlayerFragment();
              playerFragment1.setArguments(bundle);
              manager.beginTransaction()
                      .replace(R.id.frmid, playerFragment1,"playerfragment")
                      .addToBackStack("fragment")
                      .commit();
          }
            catch (Exception e){

                final FragmentManager manager=getSupportFragmentManager();
                Bundle  bundle= new Bundle();
                bundle.putString("status","online");
                bundle.putString("title",modalClass.getTitle());
                bundle.putString("artist",modalClass.getArtist());
                bundle.putString("duration",modalClass.getDuration());
                bundle.putString("imageurl",modalClass.getImageurl());
                PlayerFragment playerFragment= new PlayerFragment();
                playerFragment.setArguments(bundle);
                manager.beginTransaction()
                        .add(R.id.frmid, playerFragment,"playerfragment")
                        .addToBackStack("playerfragment")
                        .commit();

            }










        Intent plyerservice= new Intent(MainActivity.this, MediaPlayerService.class);

        plyerservice.putExtra("mediaurl",Constants.SERVERMUSIC+modalClass.getId());


        startService(plyerservice);


















    }

    @Override
    protected void onResume() {

        super.onResume();



    }

    public void  playmusicoffline(int position){
        PLAYCOUNT++;

        if (PLAYCOUNT % 5 == 0){
            showrate();
        }

        ONLINESONG=0;

        currentpos=position;
        final OfflineModalClass modalClass = listoffline.get(position);


        progressBar.setVisibility(View.VISIBLE);
        homeplay.setVisibility(View.GONE);
        homepause.setVisibility(View.GONE);
        homeplayerlayout.setVisibility(View.VISIBLE);
        homeplayerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (PLAYERSTATUS.equals("PLAYING")){

                    FragmentManager manager=getSupportFragmentManager();
                    Bundle  bundle= new Bundle();
                    bundle.putString("title",modalClass.getFilename());
                    bundle.putString("status","offline");
                    bundle.putString("duration",modalClass.getDuration());
                    try {
                        PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
                        playerFragment.setArguments(bundle);
                        manager.beginTransaction()
                                .remove(playerFragment)
//                    .replace(R.id.frmid, playerFragment,"playerfragment")
                                .commit();


                        PlayerFragment playerFragment1= new PlayerFragment();
                        playerFragment1.setArguments(bundle);
                        manager.beginTransaction()
                                .replace(R.id.frmid, playerFragment1,"playerfragment")
                                .addToBackStack("fragment")
                                .commit();
                    }
                    catch (Exception e ){
                        System.out.println(e);
                    }
                }

                else {


                    final FragmentManager manager=getSupportFragmentManager();
                    Bundle  bundle= new Bundle();
                    bundle.putString("title",modalClass.getFilename());
                    bundle.putString("status","offline");
                    bundle.putString("duration",modalClass.getDuration());

                    try {



                        PlayerFragment playerFragment1= new PlayerFragment();
                        playerFragment1.setArguments(bundle);
                        manager.beginTransaction()
                                .replace(R.id.frmid, playerFragment1,"playerfragment")
                                .addToBackStack("fragment")
                                .commit();
                    }
                    catch (Exception e ){
                        System.out.println(e);
                    }


                }



//



            }
        });

        final FragmentManager manager=getSupportFragmentManager();
        Bundle  bundle= new Bundle();
        bundle.putString("title",modalClass.getFilename());
        bundle.putString("status","offline");
        bundle.putString("duration",modalClass.getDuration());

        try {
            PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
            playerFragment.setArguments(bundle);
            manager.beginTransaction()
                    .remove(playerFragment)
//                    .replace(R.id.frmid, playerFragment,"playerfragment")
                    .commit();


            PlayerFragment playerFragment1= new PlayerFragment();
            playerFragment1.setArguments(bundle);
            manager.beginTransaction()
                    .replace(R.id.frmid, playerFragment1,"playerfragment")
                    .addToBackStack("fragment")
                    .commit();
        }
        catch (Exception e ){


            PlayerFragment playerFragment1= new PlayerFragment();
            playerFragment1.setArguments(bundle);
            manager.beginTransaction()
                    .replace(R.id.frmid, playerFragment1,"playerfragment")
                    .addToBackStack("fragment")
                    .commit();
            System.out.println(e);
        }


        Intent plyerservice= new Intent(MainActivity.this, MediaPlayerService.class);
        plyerservice.putExtra("mediaurl",modalClass.getFilepath());
        startService(plyerservice);

         PLAYERSTATUS="LOADING";



        hometitle.setText(modalClass.getFilename());
        homeartist.setText(modalClass.getSize());



        // Media PlayerActivity







    }
    public void  showeq(){
        int sessionId = mp.getAudioSessionId();
        DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                .setAudioSessionId(sessionId)
                .themeColor(ContextCompat.getColor(this, R.color.red_1))
                .textColor(ContextCompat.getColor(this, R.color.red_2))
                .accentAlpha(ContextCompat.getColor(this, R.color.red_3))
                .darkColor(ContextCompat.getColor(this, R.color.red_4))
                .setAccentColor(ContextCompat.getColor(this, R.color.red_5))
                .build();
        fragment.show(getSupportFragmentManager(), "eq");
    }

    public void  pausemusic(){

        Intent intent = new Intent("fando");
        intent.putExtra("status", "pause");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

       PLAYERSTATUS="STOPING";

        homepause.setVisibility(View.GONE);
        homeplay.setVisibility(View.VISIBLE);

        MainActivity.this.sendBroadcast(new Intent("playpause"));



    }
    public void  resumeemusic(){
        Intent intent = new Intent("fando");
        intent.putExtra("status", "resume");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);
        PLAYERSTATUS="PLAYING";
        homeplay.setVisibility(View.GONE);
        homepause.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);

        try {
            PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
            fragment.togglebuttonplay();
            fragment.hideprogressbar();
        }

        catch (Exception e){
            System.out.println(e);
        }

    }

    public void next(){
        PLAYERSTATUS="LOADING";
        if (ONLINESONG==1){

            if (currentpos<(currentlistplay.size()-1)){
                playmusic(currentpos+1,currentlistplay);

            }

        }

        else {

            if (currentpos<(listoffline.size()-1)){
                playmusicoffline(currentpos+1);
            }


        }




    }


    public void repeat(){
        if (ONLINESONG==1){

            playmusic(currentpos,currentlistplay);

        }

        else {

            playmusicoffline(currentpos);


        }




    }


    public void previous(){
        if (ONLINESONG==1){

            if (currentpos<currentlistplay.size())
                playmusic(currentpos-1,currentlistplay);

        }

        else {

            if (currentpos<listoffline.size())
                playmusicoffline(currentpos-1);

        }
    }
    public void  showsearchdialog(){

        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        for (final Fragment fragmento: allFragments) {
            if (fragmento instanceof TopsongFragment){

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Find a Song");
        alert.setMessage("Input Search Query");
// Set an EditText view to get user input
        final EditText input = new EditText(MainActivity.this);

        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();
                ((TopsongFragment) fragmento).findsongs(result);



                //do what you want with your result
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();



            }

            else {
                viewPager.setCurrentItem(0);

            }


        }

    }

    public void updatelist(){
        adapter.notifyDataSetChanged();
    }

    public void  initrealm(){
        Realm.init(MainActivity.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

    }
    public  void addtoplaylits(){
        if (ONLINESONG==1){
            realmHelper = new RealmHelper(realm,getApplication());
            realmHelper.saveplaylists(currentsongmodel);
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"Added to Playlists",LENGTH_LONG).show();



        }else {

            Toast.makeText(getApplicationContext(),"This is Local Song",LENGTH_LONG).show();
        }


    }
    public  void addtoplaylitssingle(SongModel songModel){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saveplaylists(songModel);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Added to Playlists",LENGTH_LONG).show();


    }


    public  void removefromplaylists(SongModel songModel){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromplaylists(songModel);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Remove From Playlists",LENGTH_LONG).show();

    }

    public  void removefromrecent(SongModel songModel){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromrecent(songModel);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Remove From Recent",LENGTH_LONG).show();

    }
    public  void addtorecent(){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saverecent(currentsongmodel);
        adapter.notifyDataSetChanged();


    }

    public List<SongModel> getrecent(){
        realmHelper = new RealmHelper(realm,getApplication());
        listrecent=  realmHelper.getAllSongsrecent();

        return listrecent;

    }

    public List<SongModel> getplaylists(){
        realmHelper = new RealmHelper(realm,getApplication());
        listrecent=  realmHelper.getallplaylists();

        return listrecent;
    }

    public void  gototab(int tab){
        FragmentManager manager=getSupportFragmentManager();

        PlayerFragment playerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
        manager.beginTransaction()
                .remove(playerFragment)
//                    .replace(R.id.frmid, playerFragment,"playerfragment")
                .addToBackStack("fragment")
                .commit();

        viewPager.setCurrentItem(tab);

    }

    public void settimer(Long end,String timer){
        new CountDownTimer(end, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent("fando");
                intent.putExtra("status", "stopmusic");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

            }
        }.start();

        Toast.makeText(getApplicationContext(),"Timer set : "+timer,Toast.LENGTH_LONG).show();

    }

    /**
     * Background Runnable thread
     */

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");

            if (fragment!=null){

                Intent intent = new Intent("fando");
                intent.putExtra("status", "getduration");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                fragment.updateTimerAndSeekbar(totalduration,currentduraiton);

            }
            // Running this thread after 10 milliseconds
            if (PLAYERSTATUS.equals("PLAYING")) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    public void  updateseekbarmp(int progress){

        double currentseek = ((double) progress/(double)MusicUtils.MAX_PROGRESS);

        int totaldura= (int) totalduration;
        int seek= (int) (totaldura*currentseek);

        Intent intent = new Intent("fando");
        intent.putExtra("status", "seek");
        intent.putExtra("seektime",seek);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);




        System.out.println("sekarang : "+seek);
//        System.out.println("sekarang pro "+progress);


    }



    @Override
    public void onBackPressed() {




        PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");

        if ((fragment ==null)){



            new AlertDialog.Builder(this)
                    .setTitle("Really Exit?")
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            showinter();
                        }
                    }).create().show();

        }

        else {
            super.onBackPressed();
        }


    }

    public void loadbanner (){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
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
    }

    public void showrate(){

        SmartRate.Rate(MainActivity.this
                , "Rate Us"
                , "Tell others what you think about this app"
                , "Continue"
                , "Please take a moment and rate us on Google Play"
                , "click here"
                , "Cancel"
                , "Thanks for the feedback"
                , Color.parseColor("#2196F3")
                , 4
        );

    }



    private void showTermServicesDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_term_of_services);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        Button declineBt = dialog.findViewById(R.id.bt_decline);
        Button acceptBt = dialog.findViewById(R.id.bt_accept);



        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        acceptBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("push", MODE_PRIVATE).edit();
                editor.putBoolean("firstTime",false);
                editor.apply();
                dialog.dismiss();
            }
        });

        declineBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public  void showinter() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Ads");
        pDialog.setCancelable(false);

        pDialog.show();


        mInterstitialAd = new InterstitialAd(this);
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
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Exit Apps");
                pDialog.setCancelable(false);
                pDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                        System.exit(0);
                        finishAffinity();
                        // change image
                    }

                }, 3000);
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
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Exit Apps");
                pDialog.setCancelable(false);
                pDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        finish();
                        finishAffinity();
                        System.exit(0);
                        // change image
                    }

                }, 3000);



                // Code to be executed when the interstitial ad is closed.
            }
        });


    }






}
