package com.example.tenarse.ui.newpost.httpUploads;

import android.os.AsyncTask;

import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.register.RegisterFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyAsyncTaskQuestion extends AsyncTask<Void, Void, String> {

    private final String url;
    private final JSONObject jsonObject;

    private final RegisterFragment registerFragment = new RegisterFragment();

    public MyAsyncTaskQuestion(String url, JSONObject jsonObject) {
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
        registerFragment.setResultRegister(result);
        if (!result.contains("false")){
            System.out.println("Escribiendo datos globales del usuario...");
            try {
                JSONObject dadesUsuariNou = new JSONObject(result);
                GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                globalDadesUser.setDadesUser(dadesUsuariNou);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}