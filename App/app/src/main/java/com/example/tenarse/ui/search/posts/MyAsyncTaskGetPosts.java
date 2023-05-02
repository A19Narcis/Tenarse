package com.example.tenarse.ui.search.posts;

import android.os.AsyncTask;

import com.example.tenarse.ui.search.users.SearchUsersFragment;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAsyncTaskGetPosts extends AsyncTask<Void, Void, String> {

    private final String URL;
    private final JSONObject jsonObject;

    public MyAsyncTaskGetPosts(String url, JSONObject jsonObject){
        this.URL = url;
        this.jsonObject = jsonObject;
    }

    @Override
    protected String doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        // Aquí puedes procesar la respuesta de la solicitud HTTP
    }
}
