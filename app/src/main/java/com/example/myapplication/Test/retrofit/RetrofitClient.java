package com.example.myapplication.Test.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient retrofitClient = null;
    private static ApiService apiService;
    private static String baseUrl = "http://49.247.146.128:80/";
    private static Gson gson;



    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public static RetrofitClient getInstance() {
        gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }

    public static ApiService getApiService() {
        return apiService;
    }
}
