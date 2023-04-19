package com.example.tenarse.ui.login;

import android.os.AsyncTask;

import com.example.tenarse.globals.GlobalDadesUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAsyncTaskLogin extends AsyncTask<Void, Void, String> {

    private final String url;
    private final JSONObject jsonObject;

    private final LoginFragment loginFragment = new LoginFragment();

    public MyAsyncTaskLogin(String url, JSONObject jsonObject) {
        this.url = url;
        this.jsonObject = jsonObject;
    }

    @Override
    protected String doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();
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
        loginFragment.setResultLogin(result);
        try {
            JSONObject dadesLogin = new JSONObject(result);
            GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
            globalDadesUser.setDadesUser(dadesLogin);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}