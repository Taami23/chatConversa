package com.example.chatconversa.Objetos;

public class UserMessage {
    private String user_id;
    private String username;
    private String user_image;
    private String user_thumbnail;

    public UserMessage(String user_id, String username, String user_image, String user_thumbnail) {
        this.user_id = user_id;
        this.username = username;
        this.user_image = user_image;
        this.user_thumbnail = user_thumbnail;
    }

    public UserMessage() {
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getUser_thumbnail() {
        return user_thumbnail;
    }

    public void setUser_thumbnail(String user_thumbnail) {
        this.user_thumbnail = user_thumbnail;
    }

    @Override
    public String toString() {
        return "UserMessage{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", user_image='" + user_image + '\'' +
                ", user_thumbnail='" + user_thumbnail + '\'' +
                '}';
    }
}
