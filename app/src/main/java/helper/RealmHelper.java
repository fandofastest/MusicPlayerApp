package helper;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ModalClass.SongModel;
import io.realm.Realm;
import io.realm.RealmResults;

public class RealmHelper {

    Realm realm;
    Context contex;

    public  RealmHelper(Realm realm,Context contex){
        this.realm = realm;
        this.contex=contex;
    }

    // untuk menyimpan data
    public void saverecent(final SongModel songModel){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm != null){
                    RealmResults<SongModel> result = realm.where(SongModel.class)
                            .equalTo("id",songModel.getId())
                            .findAll();

                    if (result.size()>0){

                        updatesongrecent(songModel.getId(),"1");
                        System.out.println("diupdate ke recent");


                    }else {


                        try {
                            songModel.setRecent("1");
                            SongModel model = realm.copyToRealm(songModel);
                            System.out.println("ditambahkan ke recent");
                        }

                        catch (Exception e){
//                        Toast.makeText(contex,"Song already exists",Toast.LENGTH_LONG).show();

                        }

                    }





                }else{
                    Log.e("ppppp", "execute: Database not Exist");
                }
            }
        });
    }


    // untuk menyimpan data
    public void saveplaylists(final SongModel songModel){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm != null){
                    RealmResults<SongModel> result = realm.where(SongModel.class)
                             .equalTo("id",songModel.getId())
                            .findAll();

                    if (result.size()>0){

                        updatesongplaylists(songModel.getId(),"1");
                        System.out.println("diupdate ke playlists");


                    }else {


                        try {
                            songModel.setInplaylists("1");
                            SongModel model = realm.copyToRealm(songModel);
                            System.out.println("ditambahkan ke playlists");
                        }

                        catch (Exception e){
//                        Toast.makeText(contex,"Song already exists",Toast.LENGTH_LONG).show();

                        }

                    }





                }else{
                    Log.e("ppppp", "execute: Database not Exist");
                }
            }
        });
    }

    // untuk memanggil semua data
    public List<SongModel> getAllSongsrecent(){

        RealmResults<SongModel> results = realm.where(SongModel.class)
                                            .equalTo("recent","1")
                                            .findAll();



        return results;
    }



    public List<SongModel> getallplaylists(){

        RealmResults<SongModel> results = realm.where(SongModel.class)
                                        .equalTo("inplaylists","1")
                                        .findAll();
        return results;
    }

    // untuk meng-update data
    public void updaterecent(final Integer id, final String imageurl, final String duration, final String artist, final String type){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SongModel model = realm.where(SongModel.class)
                        .equalTo("id", id)
                        .findFirst();
                model.setId(id);
                model.setArtist(artist);
                model.setDuration(duration);
                model.setImageurl(imageurl);
                model.setType(type);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("pppp", "onSuccess: Update Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    // untuk meng-update data
    public void updatesongplaylists(final Integer id, final String inplaylists){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SongModel model = realm.where(SongModel.class)
                        .equalTo("id", id)
                        .findFirst();
                model.setInplaylists(inplaylists);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("pppp", "onSuccess: Update Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    // untuk meng-update data
    public void updatesongrecent(final Integer id, final String recent){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SongModel model = realm.where(SongModel.class)
                        .equalTo("id", id)
                        .findFirst();
                model.setRecent(recent);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.e("pppp", "onSuccess: Update Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    public void removefromplaylists(SongModel songModel){
        updatesongplaylists(songModel.getId(),"0");

    }

    public void removefromrecent(SongModel songModel){
        updatesongrecent(songModel.getId(),"0");

    }



    // untuk menghapus data
    public void delete(Integer id){
        final RealmResults<SongModel> model = realm.where(SongModel.class).equalTo("id", id).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                model.deleteFromRealm(0);
            }
        });
    }
}
