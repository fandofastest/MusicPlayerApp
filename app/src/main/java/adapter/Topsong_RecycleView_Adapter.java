package adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import ModalClass.SongModel;
import cn.pedant.SweetAlert.SweetAlertDialog;
import simplemusicuiux.musicapp.MainActivity;
import simplemusicuiux.musicapp.MusicUtils;
import simplemusicuiux.musicapp.R;



public class Topsong_RecycleView_Adapter extends RecyclerView.Adapter<Topsong_RecycleView_Adapter.MyViewHolder> {

    Context context;
    public static List<SongModel> tredingModalClassList;
    InterstitialAd mInterstitialAd;
    SweetAlertDialog pDialog;



public Topsong_RecycleView_Adapter(Context mainActivityContacts, List<SongModel> listModalClassList) {
        this.tredingModalClassList = listModalClassList;
        this.context = mainActivityContacts;
        }

@Override
public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_topsong, parent, false);



        return new MyViewHolder(itemView);


        }

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
@Override
public void onBindViewHolder(final MyViewHolder holder, final int position){


    final SongModel modalClass = tredingModalClassList.get(position);

    MusicUtils musicUtils= new MusicUtils();
        holder.title.setText(modalClass.getTitle());
        holder.artists.setText(modalClass.getArtist());
        holder.duration.setText(musicUtils.milliSecondsToTimer(Long.parseLong(modalClass.getDuration())));
             Glide
            .with(context)
            .load(modalClass.getImageurl())
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher)
            .into(holder.image);


        holder.mainly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showinter(position);








            }
        });

        holder.addtopl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    ((MainActivity)context).addtoplaylitssingle(modalClass);
                }
            }
        });

        }




@Override
public int getItemCount() {
        return tredingModalClassList.size();
        }

public class MyViewHolder extends RecyclerView.ViewHolder {


    ImageView image,addtopl;
    TextView title,artists,duration;
    LinearLayout mainly;


    public MyViewHolder(View view) {
        super(view);


        image = view.findViewById(R.id.imageview);
        title = view.findViewById(R.id.txttitle);
        artists = view.findViewById(R.id.txtartists);
        duration=view.findViewById(R.id.txtduration);
        mainly=view.findViewById(R.id.mainly);
        addtopl=view.findViewById(R.id.addtoplaylists);


    }

}

    public  void showinter(final int position) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading Ads");
        pDialog.setCancelable(false);

        pDialog.show();


        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.interads));
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
                if (context instanceof MainActivity) {
                    ((MainActivity)context).playmusic(position,tredingModalClassList);
                }


                if (context instanceof MainActivity) {
                    ((MainActivity)context).addtorecent();
                }


                if (context instanceof MainActivity) {
                    ((MainActivity)context).updatelist();
                }

                System.out.println(position);

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
                if (context instanceof MainActivity) {
                    ((MainActivity)context).playmusic(position,tredingModalClassList);
                }


                if (context instanceof MainActivity) {
                    ((MainActivity)context).addtorecent();
                }


                if (context instanceof MainActivity) {
                    ((MainActivity)context).updatelist();
                }

                System.out.println(position);
                // Code to be executed when the interstitial ad is closed.
            }
        });





    }
}
