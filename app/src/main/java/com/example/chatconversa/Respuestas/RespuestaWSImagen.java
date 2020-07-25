package com.example.chatconversa.Respuestas;

import com.example.chatconversa.Objetos.Image;

public class RespuestaWSImagen {
    private Integer status_code;
    private String message;
    private Image image;

    public RespuestaWSImagen(Integer status_code, String message, Image image) {
        this.status_code = status_code;
        this.message = message;
        this.image = image;
    }

    public RespuestaWSImagen() {
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "RespuestaWSImagen{" +
                "status_code=" + status_code +
                ", message='" + message + '\'' +
                ", image=" + image +
                '}';
    }
}
