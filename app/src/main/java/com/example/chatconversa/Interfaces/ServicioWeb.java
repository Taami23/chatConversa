package com.example.chatconversa.Interfaces;

import com.example.chatconversa.Respuestas.RespuestaWSImagen;
import com.example.chatconversa.Respuestas.RespuestaWSLogin;
import com.example.chatconversa.Respuestas.RespuestaWSLoguot;
import com.example.chatconversa.Respuestas.RespuestaWSMessages;
import com.example.chatconversa.Respuestas.RespuestaWSRegister;
import com.example.chatconversa.Respuestas.RespuestaWSSendMessage;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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
    Call<RespuestaWSLoguot> logout(@Header("Authorization") String token, @Field("user_id") String user_id,
                                   @Field("username") String username);

    @FormUrlEncoded
    @POST("get")
    Call<RespuestaWSMessages> messages(@Header("Authorization") String token, @Field("user_id") String user_id,
                                       @Field("username") String username);

//    @Multipart
//    @POST("send")
//    Call<RespuestaWSSendMessage> send(@Header("Authorization") String token, @Part("user_id") String user_id,
//                                      @Part("username") String username, @Part("message")String message, @Query("image") MultipartBody.Part user_image, @Part("latitude") Double latitude, @Part("longitude") Double longitude);
    @FormUrlEncoded
    @POST("send")
    Call<RespuestaWSSendMessage> send(@Header("Authorization") String token, @Field("user_id") String user_id,
                                      @Field("username") String username, @Field("message")String message, @Field("latitude") Double latitude, @Field("longitude") Double longitude);


    @Multipart
    @POST("load/image")
    Call<RespuestaWSImagen> image(@Header("Authorization") String token, @Part("user_id") String user_id,
                                  @Part("username") String username, @Part MultipartBody.Part user_image);
}
