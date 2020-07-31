package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import ModalClass.OfflineModalClass;
import simplemusicuiux.musicapp.MainActivity;
import simplemusicuiux.musicapp.R;


public class OfflineMusic_RecycleView_Adapter extends RecyclerView.Adapter<OfflineMusic_RecycleView_Adapter.MyViewHolder> {

    Context context;
    private List<OfflineModalClass> offlineModalClassList;




    public OfflineMusic_RecycleView_Adapter(Context mainActivityContacts, List<OfflineModalClass> listModalClassList) {
        this.offlineModalClassList = listModalClassList;
        this.context = mainActivityContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offline_music, parent, false);



        return new MyViewHolder(itemView);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
         OfflineModalClass modalClass = offlineModalClassList.get(position);
        holder.name1.setText(modalClass.getFilename());


        holder.size.setText(size(Integer.parseInt(modalClass.getSize())));
        holder.lyoffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (context instanceof MainActivity) {
                    ((MainActivity)context).playmusicoffline(position);
                }


            }
        });

    }

    @Override
    public int getItemCount() {
        return offlineModalClassList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name1,size;
        LinearLayout lyoffline;

        public MyViewHolder(View view) {
            super(view);


            name1 = (TextView) view.findViewById(R.id.txttitle);
            size=view.findViewById(R.id.size);
            lyoffline=view.findViewById(R.id.lyoffline);



        }

    }

    public String size(int size){
        String hrSize = "";
        double m = size/1048576.0;
        DecimalFormat dec = new DecimalFormat("0.00");

        if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else {
            hrSize = dec.format(size).concat(" KB");
        }
        return hrSize;
    }
}
