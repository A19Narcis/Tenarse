package com.example.tenarse.widgets;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.tenarse.R;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String result;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        
        readIntent();

        String dest_uri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        UCrop.Options options = new UCrop.Options();

        UCrop.of(fileUri, Uri.fromFile(new File(getCacheDir(), dest_uri)))
                .withOptions(options)
                .withAspectRatio(1, 1)
                .useSourceImageAspectRatio()

                .withMaxResultSize(2000, 2000)
                .start(CropperActivity.this);
    }

    private void readIntent() {
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            result = intent.getStringExtra("DATA");
            fileUri=Uri.parse(result);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP){
            final Uri resultUri = UCrop.getOutput(data);
            Intent returnIntent = new Intent();
            returnIntent.putExtra("RESULT", resultUri+"");
            setResult(-1, returnIntent);
            finish();
        }else if(resultCode==UCrop.RESULT_ERROR){
            final  Throwable cropError=UCrop.getError(data);
        }
    }
}