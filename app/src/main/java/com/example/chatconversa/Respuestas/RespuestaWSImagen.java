package com.example.chatconversa.Respuestas;

import com.example.chatconversa.Objetos.Data;


public class RespuestaWSImagen {
    private Integer status_code;
    private String message;
    private Data data;

    public RespuestaWSImagen() {
    }

    public RespuestaWSImagen(Integer status_code, String message, Data data) {
        this.status_code = status_code;
        this.message = message;
        this.data = data;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespuestaWSImagen{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
