package com.example.tenarse.ui.newpost.images;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.ui.newpost.NewpostFragment;
import com.example.tenarse.ui.newpost.adapters.HashtagAdapter;
import com.example.tenarse.widgets.CropperActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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

public class FragmentAddImages extends Fragment{
    ImageView image;
    CardView cardView;

    ScrollView scrollView;

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapter;

    RecyclerView recyclerView;

    HashtagAdapter hashtagAdapter;

    Button submitBtnImg;

    TextView errorText;
    TextView errorFaltanCampos;
    EditText postText;
    ArrayList<String> arrayRecycler = new ArrayList<>();

    String pathImg = "";

    ApiService apiService;

    ImageView imagenView;

    TextView longitudTexto;

    boolean textValid;

    private static final int GALLERY_REQUEST_CODE = 1;

    @SuppressLint({"MissingInflatedId", "CutPasteId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_add_images, container, false);

        image = rootView.findViewById(R.id.rv_post_image);
        image.setClickable(true);
        image.setFocusable(true);
        cardView = rootView.findViewById(R.id.card_view_rv_image);
        imagenView = rootView.findViewById(R.id.rv_post_image);
        scrollView = rootView.findViewById(R.id.scrollV_add_images);
        submitBtnImg = rootView.findViewById(R.id.submitBtnImg);
        postText = rootView.findViewById(R.id.postText);
        errorText = rootView.findViewById(R.id.errorText);
        errorFaltanCampos = rootView.findViewById(R.id.errorFaltanCampos);
        longitudTexto = rootView.findViewById(R.id.textoLength);

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
                if (errorText.getVisibility() == View.VISIBLE){
                    errorText.setVisibility(View.GONE);
                }
            }
        });

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int actionID = 6;
                actionId = actionID;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = autoCompleteTextView.getText().toString();
                    if (userInput.length() > 0 && userInput.startsWith("#") || userInput.length() == 0){
                        if (errorText.getVisibility() == View.VISIBLE){
                            errorText.setVisibility(View.GONE);
                        }
                        if (userInput.length() > 0 && userInput.startsWith("#")){
                            arrayRecycler.add(userInput);
                            hashtagAdapter.notifyItemInserted(arrayRecycler.size() - 1);
                            autoCompleteTextView.setText("");
                        }
                    } else if (!userInput.startsWith("#")) {
                        errorText.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                return false;
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

        imagenView.setOnClickListener(new View.OnClickListener() {
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

                if (postText.getText().toString().length() == 0 || pathImg.length() == 0 || !textValid){
                    errorFaltanCampos.setVisibility(View.VISIBLE);
                } else {
                    if (errorFaltanCampos.getVisibility() == View.VISIBLE){
                        errorFaltanCampos.setVisibility(View.GONE);
                    }

                    File file = new File(pathImg);
                    // Crear un objeto JSONObject y agregar los campos necesarios
                    String idUser = "null";
                    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                    JSONObject jsonGDU = globalDadesUser.getDadesUser();
                    JSONObject json = new JSONObject();
                    JSONArray comments = new JSONArray();
                    JSONArray hashtags = new JSONArray(arrayRecycler);

                    try {
                        idUser = jsonGDU.getString("_id");
                        json.put("type", "image");
                        json.put("title", "");
                        json.put("text", postText.getText().toString());
                        json.put("comments", comments);
                        json.put("owner", jsonGDU.getString("_id"));
                        json.put("hashtags", hashtags);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Crear un RequestBody a partir del JSON
                    RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
                    RequestBody postImg = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("post", idUser, postImg);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "postImage");

                    // Enviar la solicitud POST con el multipart y el JSON como parte del cuerpo de la solicitud
                    Call<ResponseBody> req = apiService.postImage(body, name, jsonBody);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            Toast.makeText(getContext(), "¡Post subido!", Toast.LENGTH_SHORT).show();

                            NewpostFragment newpostFragment = (NewpostFragment) getParentFragment();
                            newpostFragment.postUploaded();

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }
            }
        });

        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                longitudTexto.setText(s.length() + "/75");
                if (s.length() > 75){
                    textValid = false;
                    longitudTexto.setTextColor(Color.RED);
                } else if (s.length() <= 75){
                    textValid = true;
                    longitudTexto.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    private void initRetrofitClient(){
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl("http://212.227.40.235:3000").client(client).build().create(ApiService.class);
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
            image.setAdjustViewBounds(true);
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
            image.setImageURI(resultUri);

            ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            cardView.setLayoutParams(layoutParams);
        }
    }


}