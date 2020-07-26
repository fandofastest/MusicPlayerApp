package ModalClass;

public class GenreModel {
    int image;
    String genrename,origenrename;
    public GenreModel(){
    }

    public int getImage() {
        return image;
    }

    public String getGenrename() {
        return genrename;
    }

    public GenreModel(int image, String genrename) {
        this.image=image;
        this.genrename=genrename;

    }

    public GenreModel(int image, String genrename, String origenrename) {
        this.image = image;
        this.genrename = genrename;
        this.origenrename = origenrename;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setGenrename(String genrename) {
        this.genrename = genrename;
    }

    public String getOrigenrename() {
        return origenrename;
    }

    public void setOrigenrename(String origenrename) {
        this.origenrename = origenrename;
    }
}
