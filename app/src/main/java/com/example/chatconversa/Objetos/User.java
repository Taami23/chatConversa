package com.example.chatconversa.Objetos;

public class User {
    private Integer id;
    private String name;
    private String lastname;
    private String run;
    private String username;
    private String email;
    private String image;
    private String thumbnail;

    public User() {
    }

    public User(Integer id, String name, String lastname, String run, String username, String email, String image, String thumbnail) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.run = run;
        this.username = username;
        this.email = email;
        this.image = image;
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", run='" + run + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}