package com.example.tenarse.ui.newpost.httpUploads;

import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostImatge {
    private static final String TAG = PostImatge.class.getSimpleName();

    public static void uploadImageAndJson(String imageUrl, String json, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        // Crear un RequestBody para la imagen
        File imageFile = new File(imageUrl);
        RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);

        // Crear un RequestBody para el JSON
        RequestBody jsonRequestBody = RequestBody.create(MediaType.parse("application/json"), json);

        // Crear una MultipartBody para combinar la imagen y el JSON
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("imagen", "imagen.jpg", imageRequestBody)
                .addFormDataPart("json", "json", jsonRequestBody)
                .build();

        // Crear una petición HTTP POST
        Request request = new Request.Builder()
                .url("10.0.2.2/addPostImage")
                .post(multipartBody)
                .build();

        // Enviar la petición al servidor
        client.newCall(request).enqueue(callback);
    }
}
