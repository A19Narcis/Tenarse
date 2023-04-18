package com.example.tenarse.ui.newpost.images;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.tenarse.R;

public class FragmentAddImages extends Fragment{

    ImageView image;
    CardView cardView;

    ScrollView scrollView;
    private float multiplicadorScaleFactor = 1.0f;

    private ScaleGestureDetector scaleGestureDetector;

    private static final int GALLERY_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_add_images, container, false);

        image = rootView.findViewById(R.id.rv_post_image);
        image.setClickable(true);
        image.setFocusable(true);
        cardView = rootView.findViewById(R.id.card_view_rv_image);
        scrollView = rootView.findViewById(R.id.scrollV_add_images);

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Pasar el evento onTouch a ScaleGestureDetector
                System.out.println("Entra touch");
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Iniciar la actividad de la galería
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtener la imagen seleccionada de la galería
            Uri selectedImage = data.getData();

            // Establecer la imagen seleccionada en el ImageView
            ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            image.setLayoutParams(layoutParams);

            image.setImageURI(selectedImage);
        }
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Obtener el factor de escala del detector
            float scaleFactor = detector.getScaleFactor();

            // Aplicar la escala solo si está dentro de un rango específico
            if (scaleFactor > 0.8 && scaleFactor < 1.2) {
                multiplicadorScaleFactor *= scaleFactor; // Actualizar el factor de escala global
                image.setScaleX(multiplicadorScaleFactor);
                image.setScaleY(multiplicadorScaleFactor);
            }

            return true; // Devuelve true para indicar que has manejado el evento
        }
    }

}
