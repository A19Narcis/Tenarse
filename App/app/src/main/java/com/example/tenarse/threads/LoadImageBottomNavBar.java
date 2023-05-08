package com.example.tenarse.threads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tenarse.R;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

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
        imageUrl = imageUrl.replace("\\", "/");
        Bitmap bitmap = null;
        boolean imageLoaded = false;

        while (!imageLoaded) {
            try {
                int targetSize = (int) (context.getResources().getDisplayMetrics().density * 50);
                bitmap = Glide.with(context.getApplicationContext())
                        .asBitmap()
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .override(targetSize, targetSize)
                        .centerCrop()
                        .into(targetSize, targetSize)
                        .get();

                imageLoaded = true; // La imagen se cargó correctamente, salir del bucle
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            Bitmap circularBitmap = getRoundedBitmap(bitmap);
            Drawable drawable = new BitmapDrawable(context.getResources(), circularBitmap);
            menuItem.setIcon(drawable);
        }
    }

    private Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2f;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
