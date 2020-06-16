package com.example.chatconversa.Respuestas;

public class RespuestaWSLoguot {
    private Integer user_id;
    private String username;

    @Override
    public String toString() {
        return "RespuestaWSLoguot{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                '}';
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RespuestaWSLoguot(Integer user_id, String username) {
        this.user_id = user_id;
        this.username = username;
    }

    public RespuestaWSLoguot() {
    }
}
