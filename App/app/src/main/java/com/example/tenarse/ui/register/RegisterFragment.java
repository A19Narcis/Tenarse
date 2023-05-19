package com.example.tenarse.ui.register;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tenarse.Login;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentRegisterBinding;
import com.example.tenarse.widgets.DatePickerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.Semaphore;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

public class RegisterFragment extends Fragment{

    Semaphore semaphore = new Semaphore(0);
    TextView iniciarSesionBtn;

    private FragmentRegisterBinding binding;

    EditText etPlannedDate;

    private String mensaje;

    private String data_usuari = "";

    private String verification_code;

    private JSONObject finalDadesUser;

    private Login loginActivity;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://212.227.40.235:3001");

            mSocket.on("respuestaAddNewUser", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    // Manejar el mensaje recibido
                    mensaje = (String) args[0];
                    mSocket.disconnect();
                    semaphore.release();
                }
            });
        } catch (URISyntaxException e) {}
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        loginActivity = (Login) getActivity();

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        iniciarSesionBtn = binding.IniciarSesionBtn;

        etPlannedDate = binding.etPlannedDate;

        OkHttpClient clientNode = new OkHttpClient();
        String url_register = "http://212.227.40.235:3000/addNewUser";

        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.etPlannedDate:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        iniciarSesionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });



        /* REGISTRO USER */
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.RegisterEditTextEmail.getText().toString().trim();
                String passwd = binding.RegisterEditTextPassword.getText().toString().trim();
                String passwd_repeat = binding.editTextRepeatPassword.getText().toString().trim();
                String username = binding.editTextUser.getText().toString().trim();
                String name = binding.editTextNombre.getText().toString().trim();
                String surname = binding.editTextApellidos.getText().toString().trim();
                String date = data_usuari;

                //Miniscula -> (?=.*[a-z])(?=.*[A-Z])
                //Mayuscula -> (?=.*[A-Z])
                //Digito -> (?=.*\d)
                //Caracter especial -> (?=.*[@#$%^&+=_\-()\[\]{}|:;,.<>/?!"\\])
                //Ha de ser como minimo de 8 de longitud i cumplir las condiciones -> [A-Za-z\d@#$%^&+=_\-()\[\]{}|:;,.<>/?!"\\]{8,}$
                String regexPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=_\\-()\\[\\]{}|:;,.<>/?!\"\\\\])[A-Za-z\\d@#$%^&+=_\\-()\\[\\]{}|:;,.<>/?!\"\\\\]{8,}$";
                //No permitir EMOJIS
                String regexEmoticionos = "[\\p{InEmoticons}]";
                boolean infoValida = true;

                // Verificar que no hay campos vacíos o com emoticonos
                if ((email.isEmpty() || email.matches(regexEmoticionos))
                        || (passwd.isEmpty()
                        || passwd.matches(regexEmoticionos))
                        || (passwd_repeat.isEmpty()
                        || passwd_repeat.matches(regexEmoticionos))
                        || (username.isEmpty()
                        || username.matches(regexEmoticionos))
                        || (name.isEmpty()
                        || name.matches(regexEmoticionos))
                        || (surname.isEmpty()
                        || surname.matches(regexEmoticionos))
                        || date.isEmpty()) {
                    infoValida = false;
                }
                if (!passwd.matches(regexPassword)){
                    infoValida = false;
                }
                if (!passwd.equals(passwd_repeat)) {
                    infoValida = false;
                }
                if (!email.contains("@")
                        || email.split("@").length != 2) {
                    infoValida = false;
                }
                if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
                        || !username.matches("[a-zA-Z0-9]+")
                        || !name.matches("[a-zA-ZáéíóúÁÉÍÓÚ]+")  // Permitir tildes en name
                        || !surname.matches("[a-zA-ZáéíóúÁÉÍÓÚ]+( [a-zA-ZáéíóúÁÉÍÓÚ]+)?")  // Permitir tildes en surname
                        || !date.matches("[0-9/]+")) {
                    infoValida = false;
                }
                if (infoValida){
                    finalDadesUser = new JSONObject();
                    try {
                        finalDadesUser.put("email", email);
                        finalDadesUser.put("passwd", passwd);
                        finalDadesUser.put("passwd_repeat", passwd_repeat);
                        finalDadesUser.put("username", username);
                        finalDadesUser.put("name", name);
                        finalDadesUser.put("surname", surname);
                        finalDadesUser.put("date", date);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putString("dadesUserJSON", finalDadesUser.toString());

                    Navigation.findNavController(v).navigate(R.id.action_registerFragment_to_verifyCodeFragment, bundle);

                } else {
                    binding.errorTextRegister.setVisibility(View.VISIBLE);
                }
            }
        });


        return root;
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                data_usuari = selectedDate;
                etPlannedDate.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Obtener la referencia a la Toolbar de la MainActivity
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        // Mostrar la Toolbar
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
        }

        binding = null;
    }
}