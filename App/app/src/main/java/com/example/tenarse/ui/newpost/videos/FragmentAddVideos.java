package com.example.tenarse.ui.newpost.videos;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentAddpostBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.ui.newpost.NewpostFragment;
import com.example.tenarse.ui.newpost.adapters.HashtagAdapter;

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


public class FragmentAddVideos extends Fragment {
    private FragmentAddpostBinding binding;

    Button submitBtnVideo;
    ImageView imageView;
    VideoView videoView;
    CardView cardView;
    String pathVideo;
    EditText postText;
    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> adapter;

    RecyclerView recyclerView;

    HashtagAdapter hashtagAdapter;

    ArrayList<String> arrayRecycler = new ArrayList<>();

    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    private static final int GALLERY_REQUEST_CODE = 1;
    ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_add_videos, container, false);

        //Check permissions between SDK 23 and 29
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
        //Check permissions for SDK 30 and above
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            if(!Environment.isExternalStorageManager()){
                try{
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getActivity().getApplicationContext().getPackageName())));
                    startActivity(intent);
                }catch (Exception exception){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            }
        }

        submitBtnVideo = rootView.findViewById(R.id.uploadVideo);
        cardView = rootView.findViewById(R.id.card_view_rv_image);
        imageView = rootView.findViewById(R.id.preopen_video);
        videoView = rootView.findViewById(R.id.rv_post_video);
        postText = rootView.findViewById(R.id.postText);
        imageView.setVisibility(View.GONE);

        autoCompleteTextView = rootView.findViewById(R.id.autoCompleteVideo);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.opciones_autocompletado));
        autoCompleteTextView.setAdapter(adapter);

        hashtagAdapter = new HashtagAdapter(arrayRecycler, adapter, getContext());
        recyclerView = rootView.findViewById(R.id.add_recyclerView_video);
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

        submitBtnVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File file = new File(pathVideo);
                    if (file.exists()) {
                        System.out.println("File ok");
                    } else {
                        System.out.println("Files does not exists");
                    }

// Crear un objeto JSONObject y agregar los campos necesarios
                    String idUser = "null";
                    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                    JSONObject jsonGDU = globalDadesUser.getDadesUser();
                    JSONObject json = new JSONObject();
                    JSONArray comments = new JSONArray();
                    JSONArray hashtags = new JSONArray(arrayRecycler);

                    try {
                        idUser = jsonGDU.getString("_id");
                        json.put("type", "video");
                        json.put("title", "");
                        json.put("text", postText.getText().toString());
                        json.put("comments", comments);
                        json.put("owner", jsonGDU.getString("username"));
                        json.put("user_img", jsonGDU.getString("url_img"));
                        json.put("hashtags", hashtags);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

// Crear un RequestBody a partir del JSON
                    RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
                    RequestBody postVideo = RequestBody.create(MediaType.parse("video/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("post", idUser+".", postVideo);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "postImage");

// Enviar la solicitud POST con el multipart y el JSON como parte del cuerpo de la solicitud
                    Call<ResponseBody> req = apiService.postVideo(body, name, jsonBody);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            if (response.code() == 200) {
                                System.out.println("Video uploaded successfully");
                            }

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
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
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
            ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            videoView.setLayoutParams(layoutParams);

            videoView.setVideoURI(selectedImage);
            pathVideo = getRealPathFromUri(selectedImage);

            ViewGroup.LayoutParams layoutParamsCardView = cardView.getLayoutParams();
            layoutParamsCardView.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParamsCardView.height = ViewGroup.LayoutParams.MATCH_PARENT;

            MediaController mediaController = new MediaController(getContext());
            videoView.setMediaController(null);
            mediaController.setAnchorView(videoView);
            cardView.setLayoutParams(layoutParamsCardView);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true); // Reproduce el video en bucle si lo deseas
                    videoView.start(); // Inicia la reproducción
                }
            });
        }
    }


    private String getRealPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        } else {
            return uri.getPath();
        }
    }

}
