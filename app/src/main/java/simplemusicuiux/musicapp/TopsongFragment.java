package simplemusicuiux.musicapp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ModalClass.GenreModel;
import ModalClass.SongModel;
import adapter.GenreAdapter;
import adapter.Topsong_RecycleView_Adapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TopsongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopsongFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<SongModel> listsongtop = new ArrayList<>();
    public  List<GenreModel> listgenre = new ArrayList<>();
    RecyclerView genre_recycleview;

    RecyclerView song_recycleview;
    Topsong_RecycleView_Adapter song_adapter;
    GenreAdapter genreAdapter;

    Context ctx;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TopsongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopsongFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TopsongFragment newInstance(String param1, String param2) {
        TopsongFragment fragment = new TopsongFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topsong, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx=getContext();


        genre_recycleview=view.findViewById(R.id.genre_recycleview);
        listgenre=new ArrayList<>();
        listgenre.add(new GenreModel(R.drawable.treding_img1,"Alternative Rock"));
        listgenre.add(new GenreModel(R.drawable.treding_img2,"Rock"));
        listgenre.add(new GenreModel(R.drawable.treding_img3,"Pop"));
        listgenre.add(new GenreModel(R.drawable.treding_img1,"Country"));
        genreAdapter= new GenreAdapter(ctx,listgenre,TopsongFragment.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx,LinearLayoutManager.HORIZONTAL,false);
        genre_recycleview.setLayoutManager(linearLayoutManager);
        genre_recycleview.setAdapter(genreAdapter);





        String customFont = "fonts/Poppins-SemiBold.ttf";
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), customFont);
        TextView textView2 = view. findViewById(R.id.txt_titlesongs);
        song_recycleview=view.findViewById(R.id.songs_recycleview);
        textView2.setTypeface(typeface);
        song_adapter = new Topsong_RecycleView_Adapter(ctx,listsongtop);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false);
        song_recycleview.setLayoutManager(layoutManager1);
        song_recycleview.setAdapter(song_adapter);


        gettopchart();




    }

    public void gettopchart(){
        String url="https://api-v2.soundcloud.com/charts?charts-top:all-music&&high_tier_only=false&kind=top&limit=100&client_id=z7xDdzwjM6kB7fmXCd06c8kU6lFNtBCT";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                linearLayout.setVisibility(View.GONE);
//                System.out.println(response);


                try {
                    JSONArray jsonArray1=response.getJSONArray("collection");

                    for (int i = 0;i<jsonArray1.length();i++){
                        JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                        JSONObject jsonObject=jsonObject1.getJSONObject("track");
                        SongModel listModalClass = new SongModel();
                        listModalClass.setId(jsonObject.getInt("id"));
                        listModalClass.setTitle(jsonObject.getString("title"));
                        listModalClass.setImageurl(jsonObject.getString("artwork_url"));
                        listModalClass.setDuration(jsonObject.getString("full_duration"));
                        listModalClass.setType("online");


                        try {
                            JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                            listModalClass.setArtist(jsonArray3.getString("artist"));

                        }
                        catch (JSONException e){
                            listModalClass.setArtist("Artist");

                        }


//                        System.out.println(jsonArray3);


                        listsongtop.add(listModalClass);
//



//                        Toast.makeText(getActivity(),id,Toast.LENGTH_LONG).show();


                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                song_adapter.notifyDataSetChanged();
//                songAdapter.notifyDataSetChanged();
                //    System.out.println("update"+listsongModalSearch);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(ctx).add(jsonObjectRequest);


    }


    public void getgenrechart(final String genre){
        listsongtop.clear();
        song_recycleview.removeAllViews();
        String url="https://api-v2.soundcloud.com/charts?genre=soundcloud:genres:"+genre+"&high_tier_only=false&kind=top&limit=100&client_id=z7xDdzwjM6kB7fmXCd06c8kU6lFNtBCT";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                linearLayout.setVisibility(View.GONE);
//                System.out.println(response);


                try {
                    JSONArray jsonArray1=response.getJSONArray("collection");

                    for (int i = 0;i<jsonArray1.length();i++){
                        JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                        JSONObject jsonObject=jsonObject1.getJSONObject("track");
                        SongModel listModalClass = new SongModel();
                        listModalClass.setId(jsonObject.getInt("id"));
                        listModalClass.setTitle(jsonObject.getString("title"));
                        listModalClass.setImageurl(jsonObject.getString("artwork_url"));
                        listModalClass.setDuration(jsonObject.getString("full_duration"));
                        listModalClass.setType("online");
                        listModalClass.setArtist(genre);


//                        System.out.println(jsonArray3);


                        listsongtop.add(listModalClass);
//



//                        Toast.makeText(getActivity(),id,Toast.LENGTH_LONG).show();


                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                song_adapter.notifyDataSetChanged();
//                songAdapter.notifyDataSetChanged();
                //    System.out.println("update"+listsongModalSearch);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }

    public void  showsearchdialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
        alert.setTitle("Find a Song");
        alert.setMessage("Input Search Query");
// Set an EditText view to get user input
        final EditText input = new EditText(ctx);

        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String result = input.getText().toString();

                findsongs(result);




                ;

                //do what you want with your result
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }



    public void findsongs(String q){
        listsongtop.clear();
        song_recycleview.removeAllViews();
        String url="https://api-v2.soundcloud.com/search/tracks?q="+q+"&client_id=z7xDdzwjM6kB7fmXCd06c8kU6lFNtBCT&limit=100";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    JSONArray jsonArray1=response.getJSONArray("collection");

                    for (int i = 0;i<jsonArray1.length();i++){
                        JSONObject jsonObject=jsonArray1.getJSONObject(i);
                        SongModel listModalClass = new SongModel();
                        listModalClass.setId(jsonObject.getInt("id"));
                        listModalClass.setTitle(jsonObject.getString("title"));
                        listModalClass.setImageurl(jsonObject.getString("artwork_url"));
                        listModalClass.setDuration(jsonObject.getString("full_duration"));
                        listModalClass.setType("online");


                        try {
                            JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                            listModalClass.setArtist(jsonArray3.getString("artist"));

                        }
                        catch (JSONException e){
                            listModalClass.setArtist("Artist");

                        }








                        listsongtop.add(listModalClass);
//
//                        System.out.println(jsonArray1);


//                        Toast.makeText(getActivity(),id,Toast.LENGTH_LONG).show();


                    }





                } catch (JSONException e) {
                    e.printStackTrace();
                }
                song_adapter.notifyDataSetChanged();
//                songAdapter.notifyDataSetChanged();
//                    System.out.println("update"+listsongtop);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }
}
