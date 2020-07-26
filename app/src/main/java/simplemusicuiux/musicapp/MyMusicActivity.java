package simplemusicuiux.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ModalClass.OfflineModalClass;
import adapter.OfflineMusic_RecycleView_Adapter;
import adapter.Playlist_RecycleView_Adapter;

public class MyMusicActivity extends AppCompatActivity {

    RecyclerView offline_recycleview,artist_recycleview,playlist_recycleview;

    OfflineMusic_RecycleView_Adapter mOffline_Adapter;
    Playlist_RecycleView_Adapter mPlaylist_Adapter;

    private ArrayList<OfflineModalClass> offlineModalClassArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);


        offline_recycleview = (RecyclerView) findViewById(R.id.offline_recycleview);
        artist_recycleview = (RecyclerView) findViewById(R.id.artist_recycleview);
        playlist_recycleview = (RecyclerView) findViewById(R.id.playlist_recycleview);






        mOffline_Adapter = new OfflineMusic_RecycleView_Adapter(MyMusicActivity.this,offlineModalClassArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MyMusicActivity.this);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        offline_recycleview.setLayoutManager(layoutManager);
        offline_recycleview.setAdapter(mOffline_Adapter);









        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(MyMusicActivity.this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        playlist_recycleview.setLayoutManager(layoutManager2);
        playlist_recycleview.setAdapter(mPlaylist_Adapter);


    }
}
