package com.example.tenarse.ui.newpost.images;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.ui.newpost.adapters.HashtagAdapter;
import com.example.tenarse.widgets.CropperActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.File;

public class FragmentAddImages extends Fragment{
    ImageView image;
    CardView cardView;

    ScrollView scrollView;

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapter;

    RecyclerView recyclerView;

    HashtagAdapter hashtagAdapter;

    Button submitBtnImg;

    ArrayList<String> arrayRecycler = new ArrayList<>();

    String pathImg;

    ApiService apiService;
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

        autoCompleteTextView = rootView.findViewById(R.id.autoCompleteImg);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.opciones_autocompletado));
        autoCompleteTextView.setAdapter(adapter);

        hashtagAdapter = new HashtagAdapter(arrayRecycler, adapter, getContext());
        recyclerView = rootView.findViewById(R.id.add_recyclerView_img);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(hashtagAdapter);

        initRetrofitClient();

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = (String) parent.getItemAtPosition(position);
                arrayRecycler.add(seleccion);
                hashtagAdapter.notifyItemInserted(arrayRecycler.size() - 1);
                adapter.remove(seleccion);
                adapter.notifyDataSetChanged();
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
                File file = new File(pathImg);
                if (file.exists()) {
                    System.out.println("EXISTEEEEE");
                } else {
                    System.out.println("NOOOOOOOOOOOOOOOOOO EXISTE");
                }

// Crear un objeto JSONObject y agregar los campos necesarios
                JSONObject json = new JSONObject();
                try {
                    json.put("id", "1234");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

// Crear un RequestBody a partir del JSON
                RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
                RequestBody postImg = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("postImage", "idUsuario", postImg);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "postImage");

// Enviar la solicitud POST con el multipart y el JSON como parte del cuerpo de la solicitud
                Call<ResponseBody> req = apiService.postImage(body, name, jsonBody);

                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        System.out.println(response.body().toString());

                        if (response.code() == 200) {
                            System.out.println("SUBIDAAAAAA");
                        }

                        Toast.makeText(getContext(), response.code() + " ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println("ERROOOOOOR");
                        Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                        t.printStackTrace();
                    }
                });

            }
        });

        return rootView;
    }

    private void initRetrofitClient(){
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000").client(client).build().create(ApiService.class);
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
            pathImg = data.getStringExtra("RESULT");

            Uri resultUri=null;
            if(pathImg!=null){
                resultUri=Uri.parse(pathImg);
            }
            String fileName = pathImg.substring(pathImg.lastIndexOf("/") + 1);
            pathImg = getContext().getCacheDir() + "/" + fileName;
            System.out.println(pathImg);
            image.setImageURI(resultUri);
            Bitmap imagenBitmap = BitmapFactory.decodeFile(resultUri.getPath());

            // Obtén la altura de la imagen en píxeles
            int alturaImagen = imagenBitmap.getHeight();
            int anchuraImagen = imagenBitmap.getWidth();
            if(anchuraImagen > alturaImagen){

            }
            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            cardView.setLayoutParams(layoutParams);
        }
    }


}