package com.example.chatconversa.Interfaces;

import com.example.chatconversa.Respuestas.RespuestaWSLogin;
import com.example.chatconversa.Respuestas.RespuestaWSLoguot;
import com.example.chatconversa.Respuestas.RespuestaWSRegister;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServicioWeb {
    @FormUrlEncoded
    @POST("create")
    Call<RespuestaWSRegister> registrer(@Field("name") String name, @Field("lastname") String lastname,
                                        @Field("run") String run, @Field("username") String username,
                                        @Field("email") String email, @Field("password") String password,
                                        @Field("token_enterprise") String token_enterprise);
    @FormUrlEncoded
    @POST("login")
    Call<RespuestaWSLogin> login (@Field("username") String usarname, @Field("password") String password,
                                  @Field("device_id") String device_id);
    @FormUrlEncoded
    @POST("logout")
    Call<RespuestaWSLoguot> logout(@Header("token") String elToken, @Field("user_id") String elId,
                                   @Field("username") String userName);
}
