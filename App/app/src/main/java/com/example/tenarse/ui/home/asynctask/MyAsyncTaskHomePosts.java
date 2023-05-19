package com.example.tenarse.ui.home.asynctask;

import android.os.AsyncTask;

import com.example.tenarse.ui.home.HomeFragment;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyAsyncTaskHomePosts extends AsyncTask<Void, Void, String> {

    private final String url;
    private final HomeFragment homeFragment = new HomeFragment();

    public MyAsyncTaskHomePosts(String url){
        this.url = url;
    }


    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
    protected void onPostExecute(String result){
        homeFragment.loadNewPosts(result);
    }
}
