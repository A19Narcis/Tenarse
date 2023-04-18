package com.example.tenarse.threads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.view.MenuItem;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

public class LoadImageBottomNavBar extends AsyncTask<String, Void, Bitmap> {
    private MenuItem menuItem;
    private Context context;

    public LoadImageBottomNavBar(MenuItem menuItem, Context context) {
        this.menuItem = menuItem;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imageUrl = params[0];
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(context.getApplicationContext())
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            menuItem.setIcon(drawable);
        }
    }
}