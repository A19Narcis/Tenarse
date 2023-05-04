package com.example.tenarse.httpRetrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @POST("/uploadfile")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("postImage") RequestBody name, @Part("PostJson") RequestBody jsonBody);

    @Multipart
    @POST("/uploadfile")
    Call<ResponseBody> postVideo(@Part MultipartBody.Part video, @Part("postImage") RequestBody name, @Part("PostJson") RequestBody jsonBody);

    @Multipart
    @POST("/updateUserWithImage")
    Call<ResponseBody> updateUserWithImage(@Part MultipartBody.Part image, @Part("postImage") RequestBody name, @Part("PostJson") RequestBody jsonBody);
}
