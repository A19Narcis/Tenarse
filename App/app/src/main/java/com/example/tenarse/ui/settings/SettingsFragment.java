package com.example.tenarse.ui.settings;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tenarse.Login;
import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentSettingsBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.register.MyAsyncTaskRegister;
import com.example.tenarse.widgets.CropperActivity;
import com.example.tenarse.widgets.DatePickerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingsFragment extends Fragment{

    private FragmentSettingsBinding binding;

    GlobalDadesUser globalDadesUser;

    JSONObject actualDadesUser;
    JSONObject removeDadesUser = new JSONObject();

    private String data_usuari = "";

    private String start_email;
    private String start_username;

    private String pathImg;

    private boolean imgEditada = false;

    private static final int GALLERY_REQUEST_CODE = 1;

    ApiService apiService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        globalDadesUser = GlobalDadesUser.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            try {
                actualDadesUser = new JSONObject(args.getString("dadesUser"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


        binding.newEmail.setFocusable(false);
        binding.newEmail.setClickable(false);


        initRetrofitClient();

        //Cargar los datos del usuario en los editText
        try {
            Picasso.with(getContext()).load(actualDadesUser.getString("url_img").replace("localhost", "10.0.2.2")).into(binding.newFotoPerfil);
            binding.newEmail.setText(actualDadesUser.getString("email"));
            binding.newUsername.setText(actualDadesUser.getString("username"));
            binding.newNombre.setText(actualDadesUser.getString("nombre"));
            binding.newApellidos.setText(actualDadesUser.getString("apellidos"));
            binding.newFecha.setText(actualDadesUser.getString("fecha_nac"));

            //Guardarse los datos que hay al empezar para ver si hay cambios

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        start_email = binding.newEmail.getText().toString();
        start_username = binding.newUsername.getText().toString();

        binding.newFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.newFecha:
                        showDatePickerDialog();
                        break;
                }
            }
        });


        binding.backToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        binding.cardViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(getActivity());
                alertaLogOut.setTitle("Cerrar Sesión");
                alertaLogOut.setMessage("¿Quieres cerrar tu sesión actual?");
                alertaLogOut.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        globalDadesUser.setDadesUser(removeDadesUser);
                        startActivity(new Intent(getActivity(), Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        getActivity().finish();
                    }
                });
                alertaLogOut.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Es queda a la pantalla
                    }
                });
                alertaLogOut.show();
            }
        });

        binding.saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newEmail = binding.newEmail.getText().toString();
                String newUsername = binding.newUsername.getText().toString();
                String newNombre = binding.newNombre.getText().toString();
                String newApellidos = binding.newApellidos.getText().toString();
                String newFehca = binding.newFecha.getText().toString();

                String regexEmoticionos = "[\\p{InEmoticons}]";

                boolean infoValida = true;

                // Verificar que no hay campos vacíos o com emoticonos
                if ((newEmail.isEmpty() || newEmail.matches(regexEmoticionos)) || (newUsername.isEmpty() || newUsername.matches(regexEmoticionos)) || (newNombre.isEmpty() || newNombre.matches(regexEmoticionos)) || (newApellidos.isEmpty() || newApellidos.matches(regexEmoticionos))) {
                    infoValida = false;
                    System.out.println("Vacio o Emojis");
                }

                if (!newEmail.contains("@") || newEmail.split("@").length != 2) {
                    System.out.println("Error email");
                    infoValida = false;
                }

                if (!newEmail.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") || !newUsername.matches("[a-zA-Z0-9]+") || !newNombre.matches("[a-zA-Z]+") || !newApellidos.matches("[a-zA-Z]+( [a-zA-Z]+)?") || !newFehca.matches("[0-9/]+")) {
                    infoValida = false;
                    System.out.println("Caracteres especiales");
                }

                //Ver si ya existe este nuevo email - username
                boolean newUsernameInput = false;
                boolean newEmailInput = false;

                if (!newUsername.equals(start_username)) {
                    newUsernameInput = true;
                }


                //Ver si el nuevo Email / Username es valido
                String resultGetUser = "false";
                if (newUsernameInput) {
                    String url_checkDades = "http://10.0.2.2:3000/checkUserExists";

                    JSONObject bodyCheck = new JSONObject();

                    try {
                        bodyCheck.put("username", binding.newUsername.getText().toString());


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    MyAsyncTask getUser = new MyAsyncTask(url_checkDades, bodyCheck);
                    getUser.execute();
                    try {
                        resultGetUser = getUser.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }


                if (imgEditada){
                    File file = new File(pathImg);

                    String idUser = "null";
                    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                    JSONObject jsonGDU = globalDadesUser.getDadesUser();
                    JSONObject json = new JSONObject();

                    try {
                        idUser = jsonGDU.getString("_id");
                        json.put("_id", actualDadesUser.getString("_id"));
                        json.put("email", newEmail);
                        json.put("username", newUsername);
                        json.put("url_img", "http://10.0.2.2:3000/uploads\\user_img\\" + actualDadesUser.getString("_id") + ".png");
                        json.put("nombre", newNombre);
                        json.put("apellidos", newApellidos);
                        json.put("fecha_nac", newFehca);
                        json.put("socket", actualDadesUser.getString("socket"));
                        json.put("followers",  actualDadesUser.getJSONArray("followers"));
                        json.put("followings",  actualDadesUser.getJSONArray("followings"));
                        json.put("publicacions",  actualDadesUser.getJSONArray("publicacions"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Crear un RequestBody a partir del JSON
                    RequestBody jsonBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
                    RequestBody postImg = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("post", idUser, postImg);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "postImage");

                    Call<ResponseBody> req = apiService.updateUserWithImage(body, name, jsonBody);

                    req.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            //Actualizar datos usuario
                            GlobalDadesUser.getInstance().setDadesUser(json);

                            ((MainActivity) getActivity()).updateUserImageBottom();

                            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Datos actualizados.", Snackbar.LENGTH_LONG);

                            // Cambiar el color de fondo
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));

                            // Cambiar el color del texto
                            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);

                            // Obtener el TextView dentro de Snackbar
                            TextView textoSnackbar = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                            textoSnackbar.setGravity(Gravity.CENTER);

                            // Mostrar Snackbar personalizado
                            snackbar.show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }else {
                    if (infoValida && (resultGetUser.contains("false")) || ((start_email.equals(newEmail) && start_username.equals(newUsername)))) {
                        // Todos los campos son válidos
                        JSONObject body = new JSONObject();

                        try {
                            body.put("id_user", actualDadesUser.getString("_id"));
                            body.put("email", newEmail);
                            body.put("username", newUsername);
                            body.put("name", newNombre);
                            body.put("surname", newApellidos);
                            body.put("date", newFehca);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String url_updateDades = "http://10.0.2.2:3000/updateUser";
                        MyAsyncTaskRegister updateUser = new MyAsyncTaskRegister(url_updateDades, body);
                        updateUser.execute();
                        String resultUpdate = null;
                        try {
                            resultUpdate = updateUser.get();
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        binding.errorTextUpdate.setVisibility(View.GONE);
                        binding.userExisteUpdate.setVisibility(View.GONE);

                        try {
                            JSONObject updatedDades = new JSONObject(resultUpdate);
                            GlobalDadesUser.getInstance().setDadesUser(updatedDades);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Datos actualizados.", Snackbar.LENGTH_LONG);

                        // Cambiar el color de fondo
                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));

                        // Cambiar el color del texto
                        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);

                        // Obtener el TextView dentro de Snackbar
                        TextView textoSnackbar = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                        textoSnackbar.setGravity(Gravity.CENTER);

                        // Mostrar Snackbar personalizado
                        snackbar.show();

                    } else if (!infoValida) {
                        binding.errorTextUpdate.setVisibility(View.VISIBLE);
                        binding.userExisteUpdate.setVisibility(View.GONE);
                    } else if (resultGetUser.contains("true")) {
                        //Ese usuario ya existe
                        binding.userExisteUpdate.setVisibility(View.VISIBLE);
                        binding.errorTextUpdate.setVisibility(View.GONE);
                    }

                }
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BottomNavigationView bottomNavigationView= getActivity().findViewById(R.id.nav_view);

        if (bottomNavigationView != null){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
        binding = null;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                data_usuari = selectedDate;
                binding.newFecha.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Obtener la imagen seleccionada de la galería
            Uri selectedImage = data.getData();

            // Establecer la imagen seleccionada en el ImageView
            ViewGroup.LayoutParams layoutParams = binding.newFotoPerfil.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            binding.newFotoPerfil.setLayoutParams(layoutParams);

            binding.newFotoPerfil.setImageURI(selectedImage);
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
            binding.newFotoPerfil.setImageURI(resultUri);

            /*.LayoutParams layoutParams = binding.cardViewSettings.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            binding.cardViewSettings.setLayoutParams(layoutParams);*/
            imgEditada = true;
        }
    }

    private void initRetrofitClient(){
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000").client(client).build().create(ApiService.class);
    }

}