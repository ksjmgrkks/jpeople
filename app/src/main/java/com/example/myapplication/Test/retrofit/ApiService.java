package com.example.myapplication.Test.retrofit;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("image-upload.php")
    Call<Result> uploadImage(@Part MultipartBody.Part File);
}
