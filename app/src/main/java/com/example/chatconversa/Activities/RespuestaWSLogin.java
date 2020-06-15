package com.example.chatconversa.Activities;

public class RespuestaWSLogin {
    private Integer status_code;
    private String message;
    private String token;
    private User data;

    public RespuestaWSLogin() {
    }

    @Override
    public String toString() {
        return "RespuestaWSLogin{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getStatus_code() {
        return status_code;
    }

    public void setStatus_code(Integer status_code) {
        this.status_code = status_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public RespuestaWSLogin(Integer status_code, String message, String token, User data) {
        this.status_code = status_code;
        this.message = message;
        this.token = token;
        this.data = data;
    }
}
