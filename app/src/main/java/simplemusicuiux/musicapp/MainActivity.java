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
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
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
import static android.widget.Toast.LENGTH_SHORT;
import static simplemusicuiux.musicapp.LocalFragment.listoffline;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmHelper realmHelper;
    MediaPlayer mp;
    public static int PLAYERSTATUS=0;
    public  static boolean LOOPINGSTATUS=false;
    public int currentpos;
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    List<SongModel> listrecent = new ArrayList<>();

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
        adapter.addFragment(new LocalFragment(), "Local Music");
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



    public void playmusic(int position){

        mp.setLooping(LOOPINGSTATUS);
        currentpos=position;
         final SongModel modalClass = tredingModalClassList.get(position);
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
                      PlayerFragment playerFragment= new PlayerFragment();
                      playerFragment.setArguments(bundle);
                      manager.beginTransaction()
                              .replace(R.id.frmid, playerFragment,"playerfragment")

                              .addToBackStack("fragment")
                              .commit();
                  }



//



              }
          });
          PLAYERSTATUS=2;



          hometitle.setText(modalClass.getTitle());
          homeartist.setText(modalClass.getArtist());
        Glide
                .with(this)
                .load(modalClass.getImageurl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(imagehomeplayer);

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
                .addToBackStack("fragment")
                .commit();

        // Media PlayerActivity

        mp.stop();
        mp.reset();
        mp.release();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setLooping(LOOPINGSTATUS);

            }
        });

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
//                pDialog.hide();
//                progressBar.setVisibility(View.GONE);

                PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");


                if (mplayer.isPlaying()) {
                    mplayer.pause();



                    homeplay.setVisibility(View.VISIBLE);
                    homepause.setVisibility(View.GONE);


//                    btnplay.setVisibility(View.VISIBLE);
//                    btnstop.setVisibility(View.GONE);
                    // Changing button image to play button

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

//                    btnplay.setVisibility(View.GONE);
//                    btnstop.setVisibility(View.VISIBLE);

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




    }


    public void  playmusicoffline(int position){

        mp.setLooping(LOOPINGSTATUS);
        currentpos=position;
        final OfflineModalClass modalClass = listoffline.get(position);

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


                    PlayerFragment playerFragment= new PlayerFragment();
                    playerFragment.setArguments(bundle);
                    manager.beginTransaction()
                            .replace(R.id.frmid, playerFragment,"playerfragment")

                            .addToBackStack("fragment")
                            .commit();
                }



//



            }
        });
        PLAYERSTATUS=2;



        hometitle.setText(modalClass.getFilename());
        homeartist.setText(modalClass.getSize());


        final FragmentManager manager=getSupportFragmentManager();
        Bundle  bundle= new Bundle();
        bundle.putString("title",modalClass.getFilename());
        bundle.putString("status","offline");
        bundle.putString("duration",modalClass.getDuration());



        PlayerFragment playerFragment= new PlayerFragment();
        playerFragment.setArguments(bundle);
        manager.beginTransaction()
                .add(R.id.frmid, playerFragment,"playerfragment")
                .addToBackStack("fragment")
                .commit();

        // Media PlayerActivity

        mp.stop();
        mp.reset();
        mp.release();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.setLooping(LOOPINGSTATUS);

            }
        });

        try {
            Uri myUri = Uri.parse(modalClass.getFilepath());
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
//                pDialog.hide();
//                progressBar.setVisibility(View.GONE);

                PlayerFragment fragment = (PlayerFragment) getSupportFragmentManager().findFragmentByTag("playerfragment");


                if (mplayer.isPlaying()) {
                    mplayer.pause();



                    homeplay.setVisibility(View.VISIBLE);
                    homepause.setVisibility(View.GONE);


//                    btnplay.setVisibility(View.VISIBLE);
//                    btnstop.setVisibility(View.GONE);
                    // Changing button image to play button

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

//                    btnplay.setVisibility(View.GONE);
//                    btnstop.setVisibility(View.VISIBLE);

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
        homeplay.setVisibility(View.GONE);
        homepause.setVisibility(View.VISIBLE);
    }

    public void next(){
        if (currentpos<tredingModalClassList.size())
        playmusic(currentpos+1);


    }


    public void previous(){
        if (currentpos>0)
        playmusic(currentpos-1);
    }
    public void  showsearchdialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Find a Song");
        alert.setMessage("Input Search Query");
// Set an EditText view to get user input
        final EditText input = new EditText(MainActivity.this);

        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();

                TopsongFragment topsongFragment = (TopsongFragment) getSupportFragmentManager()
                        .getFragments()
                        .get(0);

                topsongFragment.findsongs(result);

                //do what you want with your result
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
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
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saveplaylists(currentsongmodel);
        adapter.notifyDataSetChanged();

    }
    public  void addtoplaylitssingle(SongModel songModel){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saveplaylists(songModel);
        adapter.notifyDataSetChanged();

    }


    public  void removefromplaylists(SongModel songModel){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromplaylists(songModel);
        adapter.notifyDataSetChanged();
    }

    public  void removefromrecent(SongModel songModel){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromrecent(songModel);
        adapter.notifyDataSetChanged();
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
}
