package com.example.chatconversa.Activities;

public class RespuestaWSRegister {
    private Integer status_code;
    private String message;
    private User data;

    @Override
    public String toString() {
        return "RespuestaWSRegister{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
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

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public RespuestaWSRegister(Integer status_code, String message, User data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
    }

    public RespuestaWSRegister() {
    }
}
