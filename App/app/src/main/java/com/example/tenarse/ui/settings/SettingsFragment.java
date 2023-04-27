package com.example.tenarse.ui.settings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.AnimationScreen;
import com.example.tenarse.Login;
import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentHomeBinding;
import com.example.tenarse.databinding.FragmentNotificacionesBinding;
import com.example.tenarse.databinding.FragmentSettingsBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.register.MyAsyncTaskRegister;
import com.example.tenarse.widgets.DatePickerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SettingsFragment extends Fragment{

    private FragmentSettingsBinding binding;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();

    JSONObject actualDadesUser = globalDadesUser.getDadesUser();
    JSONObject removeDadesUser = new JSONObject();

    private String data_usuari = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Cargar los datos del usuario en los editText
        try {
            Picasso.with(getContext()).load(actualDadesUser.getString("url_img").replace("localhost", "10.0.2.2")).into(binding.newFotoPerfil);
            binding.newEmail.setText(actualDadesUser.getString("email"));
            binding.newUsername.setText(actualDadesUser.getString("username"));
            binding.newNombre.setText(actualDadesUser.getString("nombre"));
            binding.newApellidos.setText(actualDadesUser.getString("apellidos"));
            binding.newFecha.setText(actualDadesUser.getString("fecha_nac"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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
                if ((newEmail.isEmpty() || newEmail.matches(regexEmoticionos)) ||  (newUsername.isEmpty() || newUsername.matches(regexEmoticionos)) || (newNombre.isEmpty() || newNombre.matches(regexEmoticionos)) || (newApellidos.isEmpty() || newApellidos.matches(regexEmoticionos)) || newFehca.isEmpty()) {
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
                String url_checkDades = "http://10.0.2.2:3000/checkUserExists";

                JSONObject bodyCheck = new JSONObject();

                try {
                    bodyCheck.put("username", binding.newUsername.getText().toString());
                    bodyCheck.put("email", binding.newEmail.getText().toString());

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MyAsyncTaskGetUser getUser = new MyAsyncTaskGetUser(url_checkDades, bodyCheck);
                getUser.execute();
                String resultGetUser = null;
                try {
                    resultGetUser = getUser.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (infoValida){
                    // Todos los campos son válidos
                    JSONObject body = new JSONObject();

                    try {
                        body.put("id_user", actualDadesUser.getString("_id"));
                        body.put("email", newEmail);
                        body.put("username", newUsername);
                        body.put("name", newNombre);
                        body.put("surname", newApellidos);
                        body.put("date", newFehca);
                    } catch (JSONException e){
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

                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            super.onDismissed(snackbar, event);
                            NavController navController = NavHostFragment.findNavController(SettingsFragment.this);
                            navController.popBackStack();
                        }
                    });

                    // Mostrar Snackbar personalizado
                    snackbar.show();

                } else if (!infoValida){

                    binding.errorTextUpdate.setVisibility(View.VISIBLE);
                } else if (resultGetUser.contains("true")){
                    //Ese usuario ya existe
                    binding.userExisteUpdate.setVisibility(View.VISIBLE);
                    binding.errorTextUpdate.setVisibility(View.GONE);
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

}