package com.example.tenarse.ui.newpost.images;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
import com.example.tenarse.widgets.CropperActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
                Bitmap imagenBitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

                // Convierte la imagen a un array de bytes
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imagenBytes = stream.toByteArray();

                // Crea una cola de solicitudes HTTP
                RequestQueue colaSolicitudes = Volley.newRequestQueue(getContext());

                // URL del servidor Node.js
                String urlServidor = "http://10.0.2.2:3000/pruebaImg";

                // Crea una solicitud POST
                StringRequest solicitud = new StringRequest(Request.Method.POST, urlServidor,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                System.out.println("CONEXIÓN OKEY");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {
                    @Override
                    public byte[] getBody() {
                        return imagenBytes; // Establece los bytes de la imagen como el cuerpo de la solicitud
                    }
                };

                // Añade la solicitud a la cola de solicitudes
                colaSolicitudes.add(solicitud);
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
            String result = data.getStringExtra("RESULT");
            Uri resultUri=null;
            if(result!=null){
                resultUri=Uri.parse(result);
            }
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