package com.example.tenarse.ui.newpost.images;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.ui.home.adapters.MultiAdapter;
import com.example.tenarse.ui.newpost.adapters.HashtagAdapter;
import com.example.tenarse.ui.newpost.httpUploads.PostImatge;
import com.example.tenarse.widgets.CropperActivity;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import android.util.Log;
import android.widget.TextView;

public class FragmentAddImages extends Fragment{

    ImageView image;
    CardView cardView;

    TextView postText;

    ScrollView scrollView;

    Button submitBtnImg;

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapter;

    RecyclerView recyclerView;

    HashtagAdapter hashtagAdapter;

    ArrayList<String> arrayRecycler = new ArrayList<>();

    String pathImgUpload;

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
        submitBtnImg = rootView.findViewById(R.id.submitBtnImg);
        postText = rootView.findViewById(R.id.postText);

        autoCompleteTextView = rootView.findViewById(R.id.autoCompleteImg);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.opciones_autocompletado));
        autoCompleteTextView.setAdapter(adapter);

        hashtagAdapter = new HashtagAdapter(arrayRecycler, adapter, getContext());
        recyclerView = rootView.findViewById(R.id.add_recyclerView_img);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(hashtagAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = (String) parent.getItemAtPosition(position);
                arrayRecycler.add(seleccion);
                hashtagAdapter.notifyItemInserted(arrayRecycler.size() - 1);
                adapter.remove(seleccion);
                adapter.notifyDataSetChanged();
                System.out.println(arrayRecycler);
                autoCompleteTextView.setText("");
                autoCompleteTextView.setHint(autoCompleteTextView.getHint());
                // Cierra la lista de autocompletado
                autoCompleteTextView.dismissDropDown();
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

        submitBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                class GSONImgModel {
                    private String text;
                    private String hashtags [];

                    public GSONImgModel(String text, String[] hashtags) {
                        this.text = text;
                        this.hashtags = hashtags;
                    }
                }
                GSONImgModel gsonImgModel = new GSONImgModel(postText.getText().toString(), arrayRecycler.toArray(new String[arrayRecycler.size()]));
                Gson gson = new Gson();
                String json = gson.toJson(gsonImgModel);
                PostImatge.uploadImageAndJson(pathImgUpload, json, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Manejar el error en caso de que la petición falle
                        System.out.println("Error al subir la imagen y el JSON");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // Manejar la respuesta del servidor
                        if (response.isSuccessful()) {
                            // La petición se realizó con éxito
                            System.out.println("Imagen y JSON subidos correctamente");
                        } else {
                            // La petición falló, manejar el error
                            System.out.println("Error al subir la imagen y el JSON: " + response.message());
                        }
                    }
                });






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
            Intent intent = new Intent(getActivity(), CropperActivity.class);
            intent.putExtra("DATA", selectedImage.toString());
            startActivityForResult(intent, 101);
        } else if (resultCode == -1 && requestCode == 101) {
            pathImgUpload = data.getStringExtra("RESULT");
            Uri resultUri=null;
            if(pathImgUpload!=null){
                resultUri=Uri.parse(pathImgUpload);
            }
            image.setImageURI(resultUri);

            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            cardView.setLayoutParams(layoutParams);
        }
    }


}
