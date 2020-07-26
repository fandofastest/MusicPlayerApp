package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ModalClass.GenreModel;
import simplemusicuiux.musicapp.R;
import simplemusicuiux.musicapp.TopsongFragment;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.MyViewHolder> {

    Context context;
    private List<GenreModel> listmodelgenre;
    TopsongFragment topsongFragment;

    public GenreAdapter(Context mainActivityContext, List<GenreModel> listmodelgenre, TopsongFragment topsongFragment) {
        this.topsongFragment=topsongFragment;
        this.listmodelgenre = listmodelgenre;
        this.context = mainActivityContext;
    }


    @NonNull
    @Override
    public GenreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.MyViewHolder holder, int position) {
        final GenreModel genreModel = listmodelgenre.get(position);
        holder.imageView.setImageResource(genreModel.getImage());
        holder.textView.setText(genreModel.getGenrename());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String genre =genreModel.getGenrename();
                String genrelower=genre.toLowerCase();
                String lineWithoutSpaces = genrelower.replaceAll("\\s+","");
                System.out.println("genrenew "+lineWithoutSpaces);
                topsongFragment.getgenrechart(lineWithoutSpaces);

            }
        });

    }

    @Override
    public int getItemCount() {
        return listmodelgenre.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);
            textView=itemView.findViewById(R.id.genrename);
        }
    }
}
