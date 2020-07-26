package simplemusicuiux.musicapp;

import android.app.TimePickerDialog;
import android.content.Intent;
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

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.bumptech.glide.Glide;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
     private ImageView btnplay,btnstop,prev,next,loopingon,loopingoff,randomb,eqbutton,addtoplaylists,share;
     private TextView txttitle,txtartis,txtdura,txttotaldura;
      private ProgressBar progressBar;
      private SeekBar seekBar;
    private MediaPlayer mediaPlayer;
    private ImageView imageView,timer;
    private String title,artist,duration="0",imageurl;
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
        randomb =view.findViewById(R.id.randombutton);
        eqbutton=view.findViewById(R.id.eqbutton);
        addtoplaylists=view.findViewById(R.id.addtoplaylists);
        share=view.findViewById(R.id.share);
        progressBar.setVisibility(View.VISIBLE);
        timer=view.findViewById(R.id.timer);





        return  view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekBar.setProgress(0);
        seekBar.setMax(MusicUtils.MAX_PROGRESS);

        MusicUtils musicUtils = new MusicUtils();

        System.out.println(duration);
        txttotaldura.setText(musicUtils.milliSecondsToTimer(Long.parseLong(duration)));
        txttitle.setText(title);
//        txttotaldura.setText(duration);
        txtartis.setText(artist);
        Glide
                .with(this)
                .load(imageurl)
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(imageView);


        if (MainActivity.PLAYERSTATUS==1){
            togglebuttonplay();
            hideprogressbar();

        }else  if (MainActivity.PLAYERSTATUS==2){

           showprogressbar();

        }
        else  if (MainActivity.PLAYERSTATUS==0){

            showprogressbar();
            togglebuttonpause();
            hideprogressbar();

        }


        eqbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("showeq");
                ((MainActivity)getActivity()).showeq();

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
                MainActivity.LOOPINGSTATUS=true;
                Toast.makeText(getContext(),"Repeat On",Toast.LENGTH_LONG).show();

            }
        });

        loopingon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopingon.setVisibility(View.GONE);
                loopingoff.setVisibility(View.VISIBLE);
                MainActivity.LOOPINGSTATUS=false;
                Toast.makeText(getContext(),"Repeat Off",Toast.LENGTH_LONG).show();
            }
        });


        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
    public void toasfragment(){
        Toast.makeText(getContext(),"Playing Music",Toast.LENGTH_LONG).show();
    }



}