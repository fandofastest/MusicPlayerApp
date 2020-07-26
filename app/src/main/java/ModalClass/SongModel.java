package ModalClass;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SongModel extends RealmObject {

    @PrimaryKey
    private Integer id;



    private String title,imageurl,duration,artist,type,inplaylists,recent;

    public SongModel(){


    }

    public SongModel(String title, int id, String imageurl, String duration, String artist, String type) {
        this.title = title;
        this.id = id;
        this.imageurl = imageurl;
        this.duration = duration;
        this.artist = artist;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInplaylists() {
        return inplaylists;
    }

    public void setInplaylists(String inplaylists) {
        this.inplaylists = inplaylists;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }
}
