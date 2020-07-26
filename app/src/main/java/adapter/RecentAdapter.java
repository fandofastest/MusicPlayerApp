package adapter;

import android.content.Context;
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

import java.util.List;

import ModalClass.SongModel;
import simplemusicuiux.musicapp.MainActivity;
import simplemusicuiux.musicapp.MusicUtils;
import simplemusicuiux.musicapp.R;


public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.MyViewHolder> {

    Context context;
    public static List<SongModel> tredingModalClassList;




    public RecentAdapter(Context mainActivityContacts, List<SongModel> listModalClassList) {
        this.tredingModalClassList = listModalClassList;
        this.context = mainActivityContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song_list, parent, false);



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

                if (context instanceof MainActivity) {
                    ((MainActivity)context).playmusic(position);
                }

                System.out.println(position);

            }
        });

        holder.removerecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof MainActivity) {
                    ((MainActivity)context).removefromrecent(modalClass);
                }

            }
        });

    }




    @Override
    public int getItemCount() {
        return tredingModalClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView image,removerecent;
        TextView title,artists,duration;
        LinearLayout mainly;


        public MyViewHolder(View view) {
            super(view);

            removerecent=view.findViewById(R.id.removefromplaylists);

            image = view.findViewById(R.id.imageview);
            title = view.findViewById(R.id.txttitle);
            artists = view.findViewById(R.id.txtartists);
            duration=view.findViewById(R.id.txtduration);
            mainly=view.findViewById(R.id.mainly);


        }

    }
}
