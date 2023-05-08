package com.example.tenarse.ui.home.asynctask;

import android.os.AsyncTask;

import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.search.users.ListElementUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAsyncTaskGetUser extends AsyncTask<Void, Void, String> {

    private final String url;
    private final JSONObject jsonObject;

    final HomeFragment homeFragment = new HomeFragment();

    public MyAsyncTaskGetUser(String url, JSONObject jsonObject) {
        this.url = url;
        this.jsonObject = jsonObject;
    }

    @Override
    protected String doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(30, TimeUnit.SECONDS) // Ajusta el tiempo de espera aquí
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url(url)
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