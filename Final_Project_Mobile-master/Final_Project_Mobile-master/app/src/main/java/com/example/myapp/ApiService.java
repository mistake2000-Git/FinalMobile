package com.example.myapp;

import com.example.myapp.module.BodyRegister;
import com.example.myapp.module.BodyUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.POST;


public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://test-demo-098.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);




    @GET("api/Question/{id}")
    Call<Question> get_question(@Path("id") int groupid);



    @POST("api/User/login")
    Call<User> loginUser(@Body BodyUser body);

    @POST("api/User/add_user_player")
    Call<User> RegisterUser(@Body BodyRegister body);

    @GET("api/User/{id}")
    Call<User> get_user(@Path("id") String user_id);

    @PUT("api/User/update_point/{id}")
    Call<User> updatePoint(@Path("id") String user_id, @Body User x);

    @GET("api/image/{id}")
    Call<IMG> get_url(@Path("id") int id);

    @GET("api/voice/{id}")
    Call<VoiceQuestion> get_voice(@Path("id") int id);

    @GET("api/find_word/{word}")
    Call<Pronun> get_pronun(@Path("word") String groupid);



}


