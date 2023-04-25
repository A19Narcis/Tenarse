package com.example.tenarse.httpRetrofit;

import okhttp3.MultipartBody;
import okhttp3.Request;
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
}
