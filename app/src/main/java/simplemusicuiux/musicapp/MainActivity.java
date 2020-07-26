package simplemusicuiux.musicapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;


import com.bullhead.equalizer.DialogEqualizerFragment;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import ModalClass.OfflineModalClass;
import ModalClass.SongModel;
import adapter.TabAdapter;
import helper.RealmHelper;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static adapter.Topsong_RecycleView_Adapter.tredingModalClassList;
import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static simplemusicuiux.musicapp.LocalFragment.listoffline;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmHelper realmHelper;
    MediaPlayer mp ;
    public static int PLAYERSTATUS=0;
    public  static  int ONLINESONG=0;
    public  static boolean LOOPINGSTATUS=false;
    public int currentpos;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Handler mHandler = new Handler();

    List<SongModel> listrecent = new ArrayList<>();

    public static  List<SongModel> currentlistplay = new ArrayList<>();


    public static SongModel currentsongmodel;


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
                if (mp.isPlaying()){
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

    }



    public void playmusic(int position ,List<SongModel> listplay){
        homeplayerlayout.setVisibility(View.VISIBLE);


        ONLINESONG=1;
        mp.setLooping(LOOPINGSTATUS);
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

                  if (mp.isPlaying()){
                        PLAYERSTATUS=1;
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
                                  .addToBackStack("fragment")
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




          hometitle.setText(modalClass.getTitle());
          homeartist.setText(modalClass.getArtist());
        Glide
                .with(this)
                .load(modalClass.getImageurl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(imagehomeplayer);



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
                      .addToBackStack("fragment")
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






        PLAYERSTATUS=2;
        mp.stop();
        mp.reset();
        mp.release();


        try {
            Uri myUri = Uri.parse(Constants.SERVERMUSIC+modalClass.getId());
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mp.prepareAsync(); //don't use prepareAsync for mp3 playback
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot load audio file", LENGTH_SHORT).show();

        }


        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                if (!mp.isPlaying()){

                }

            }
        }.start();



        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPrepared(MediaPlayer mplayer) {

                PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");
                if (mplayer.isPlaying()) {
                    mplayer.pause();
                    homeplay.setVisibility(View.VISIBLE);
                    homepause.setVisibility(View.GONE);
                } else {
                    System.out.println("cekdata" +fragment);
                    if (fragment!=null){
                        fragment.togglebuttonplay();
                        fragment.hideprogressbar();
                    }

                    PLAYERSTATUS=1;
                    mplayer.start();
                    progressBar.setVisibility(View.GONE);
                    homeplay.setVisibility(View.GONE);
                    homepause.setVisibility(View.VISIBLE);
                    mHandler.post(mUpdateTimeTask);


                }

            }
        });

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                System.out.println("buffers "+i);
            }
        });

        mp.prepareAsync();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setLooping(LOOPINGSTATUS);
                next();

            }
        });




    }

    @Override
    protected void onResume() {

        super.onResume();



    }

    public void  playmusicoffline(int position){
        homeplayerlayout.setVisibility(View.VISIBLE);
        ONLINESONG=0;

        currentpos=position;
        final OfflineModalClass modalClass = listoffline.get(position);



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
                        .addToBackStack("fragment")
                        .commit();

                PlayerFragment playerFragment1= new PlayerFragment();
                playerFragment1.setArguments(bundle);
                manager.beginTransaction()
                        .replace(R.id.frmid, playerFragment1,"playerfragment")
                        .addToBackStack("fragment")
                        .commit();



            }
            catch (Exception e){



                PlayerFragment playerFragment1= new PlayerFragment();
                playerFragment1.setArguments(bundle);
                manager.beginTransaction()
                        .replace(R.id.frmid, playerFragment1,"playerfragment")
                        .addToBackStack("fragment")
                        .commit();

            }






//        else
//        {
//            final FragmentManager manager=getSupportFragmentManager();
//            Bundle  bundle= new Bundle();
//            bundle.putString("title",modalClass.getFilename());
//            bundle.putString("status","offline");
//            bundle.putString("duration",modalClass.getDuration());
//
//
//
//            PlayerFragment playerFragment= new PlayerFragment();
//            playerFragment.setArguments(bundle);
//            manager.beginTransaction()
//                    .replace(R.id.frmid, playerFragment,"playerfragment")
//                    .addToBackStack(null)
//                    .commit();
//        }





        progressBar.setVisibility(View.VISIBLE);
        homeplay.setVisibility(View.GONE);
        homepause.setVisibility(View.GONE);
        homeplayerlayout.setVisibility(View.VISIBLE);
        homeplayerlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mp.isPlaying()){
                    PLAYERSTATUS=1;
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
                                .addToBackStack("fragment")
                                .commit();

                        PlayerFragment playerFragment1= new PlayerFragment();
                        playerFragment1.setArguments(bundle);
                        manager.beginTransaction()
                                .replace(R.id.frmid, playerFragment1,"playerfragment")
                                .addToBackStack("fragment")
                                .commit();



                    }
                    catch (Exception e){



                        PlayerFragment playerFragment1= new PlayerFragment();
                        playerFragment1.setArguments(bundle);
                        manager.beginTransaction()
                                .replace(R.id.frmid, playerFragment1,"playerfragment")
                                .addToBackStack("fragment")
                                .commit();

                    }
                }




//



            }
        });
        PLAYERSTATUS=2;



        hometitle.setText(modalClass.getFilename());
        homeartist.setText(modalClass.getSize());




        // Media PlayerActivity

        mp.stop();
        mp.reset();
        mp.release();


        try {
            Uri myUri = Uri.parse(modalClass.getFilepath());
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setLooping(LOOPINGSTATUS);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot load audio file", LENGTH_SHORT).show();

        }
        new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                if (!mp.isPlaying()){

                }

            }
        }.start();



        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onPrepared(MediaPlayer mplayer) {
//                pDialog.hide();
//                progressBar.setVisibility(View.GONE);

                PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");


                if (mplayer.isPlaying()) {
                    mplayer.pause();
                    homeplay.setVisibility(View.VISIBLE);
                    homepause.setVisibility(View.GONE);
                } else {
//                    mHandler.post(mUpdateTimeTask);
                    System.out.println("cekdata" +fragment);
                    if (fragment!=null){
                        fragment.togglebuttonplay();
                        fragment.hideprogressbar();

                    }
                    PLAYERSTATUS=1;
                    mplayer.start();
                    progressBar.setVisibility(View.GONE);
                    homeplay.setVisibility(View.GONE);
                    homepause.setVisibility(View.VISIBLE);
                    mHandler.post(mUpdateTimeTask);
                }

            }
        });

        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                System.out.println("buffers "+i);



            }
        });

        mp.prepareAsync();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setLooping(LOOPINGSTATUS);
                next();

            }
        });




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
        mp.pause();


        homepause.setVisibility(View.GONE);
        homeplay.setVisibility(View.VISIBLE);


    }
    public void  resumeemusic(){
        mp.start();
        mHandler.post(mUpdateTimeTask);

        homeplay.setVisibility(View.GONE);
        homepause.setVisibility(View.VISIBLE);

    }

    public void next(){
        if (ONLINESONG==1){

            if (currentpos<currentlistplay.size()){
                playmusic(currentpos+1,currentlistplay);

            }

        }

        else {

            if (currentpos<listoffline.size()){
                playmusicoffline(currentpos+1);
            }


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
                mp.release();

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
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();
            if (fragment!=null){

                fragment.updateTimerAndSeekbar(totalDuration,currentDuration);

            }
            // Running this thread after 10 milliseconds
            if (mp.isPlaying()) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    public void  updateseekbarmp(int progress){

        double currentseek = ((double) progress/(double)MusicUtils.MAX_PROGRESS);
        mp.pause();
        int totaldura= mp.getDuration();
        int seek= (int) (totaldura*currentseek);
        mp.seekTo(seek);
        mp.start();

        System.out.println("sekarang : "+seek);
//        System.out.println("sekarang pro "+progress);


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
