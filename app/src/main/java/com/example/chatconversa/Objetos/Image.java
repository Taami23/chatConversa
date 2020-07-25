package com.example.chatconversa.Objetos;

public class Image {
    private String image;
    private String thumbnail;

    public Image(String image, String thumbnail) {
        this.image = image;
        this.thumbnail = thumbnail;
    }

    public Image() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Image{" +
                "image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
