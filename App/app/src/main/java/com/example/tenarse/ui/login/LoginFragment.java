package com.example.tenarse.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentLoginBinding;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.register.MyAsyncTaskRegister;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

public class LoginFragment extends Fragment {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button googleBtn;
    private Button loginBtn;

    TextView registrarseBtn;

    TextView test;

    private String token = null;

    private FragmentLoginBinding binding;

    private Object resultLogin;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        loginBtn = binding.buttonLogin;

        registrarseBtn = binding.registrarseBtn;

        OkHttpClient clientNode = new OkHttpClient();
        String url_login = "http://10.0.2.2:3000/getUser";

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* LOGIN con MongoDB */
                String email_username = binding.editTextEmailUsername.getText().toString();
                String passwd = binding.editTextPassword.getText().toString();

                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("email_username", email_username);
                    jsonBody.put("password", passwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MyAsyncTaskLogin loginUser = new MyAsyncTaskLogin(url_login, jsonBody);
                loginUser.execute();
                String resultLogin = null;
                try {
                    resultLogin = loginUser.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }


                System.out.println(resultLogin);

                if (!resultLogin.contains("{\"username\":false}")){
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("infoUser", resultLogin);
                    editor.apply();
                    binding.errorLoginText.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().finish();
                } else {
                    binding.errorLoginText.setVisibility(View.VISIBLE);
                }


            }
        });

        registrarseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("473605537300-nf5611uhudkvb5qe7as91131c4fsgvcf.apps.googleusercontent.com")
                .build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        googleBtn = binding.googleBtn1;

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        return root;
    }


    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                /* Con estos datos ver si este usuario ya esta registrado */
                /* Si esta registrado simplemente logea */
                /* Si no lo esta, creo una nueva cuenta con los datos de GoogleAccount */
                /* Si el usuario se `registra` iniciando sesion con Google ese correo no lo podra editar */
                /* Si ese correo no esta registrado pero el nombre de usuario si al nombre de usuario nuevo se le añade ddMMyyyyHHmmssSSS para diferenciar */
                if(checkIfEmailIsUsed(account.getEmail())){
                    String username = account.getEmail().substring(0, account.getEmail().indexOf("@"));
                    boolean isUsernameUsed = checkifUsernameIsUsed(username);
                    JSONObject bodyRegister = new JSONObject();

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            token = task.getResult();

                            try {
                                bodyRegister.put("email", account.getEmail());
                                bodyRegister.put("passwd", "");
                                bodyRegister.put("passwd_repeat", "");
                                if (isUsernameUsed){
                                    bodyRegister.put("username", username);
                                } else {
                                    // Obtener la fecha y hora actual
                                    Date date = new Date();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssSSS");
                                    String usernameWithDate = dateFormat.format(date);
                                    bodyRegister.put("username", username + usernameWithDate);
                                }
                                bodyRegister.put("name", account.getGivenName());
                                bodyRegister.put("surname", account.getFamilyName());
                                bodyRegister.put("date", " ");
                                bodyRegister.put("url_img", account.getPhotoUrl());
                                bodyRegister.put("token_id", token);
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                            //Registrar el correo
                            String url_register = "http://10.0.2.2:3000/addNewUser";
                            MyAsyncTaskRegister registerUser = new MyAsyncTaskRegister(url_register, bodyRegister);
                            registerUser.execute();
                            String resultRegister = null;
                            try {
                                resultRegister = registerUser.get();
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            Snackbar snackbar = Snackbar.make(binding.getRoot(), "Registro completado exitosamente.", Snackbar.LENGTH_LONG);

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

                                    String urlGetUser = "http://10.0.2.2:3000/getSelectedUser";
                                    JSONObject body = new JSONObject();
                                    try {
                                        body.put("username", bodyRegister.getString("username"));
                                        body.put("google", true);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                    MyAsyncTaskGetUser getUser = new MyAsyncTaskGetUser(urlGetUser, body);
                                    getUser.execute();
                                    String resultGetUserRegistered = null;
                                    try {
                                        resultGetUserRegistered = getUser.get();
                                    } catch (ExecutionException | InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    System.out.println(resultGetUserRegistered);

                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("infoUser", resultGetUserRegistered);
                                    editor.apply();


                                    startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    getActivity().finish();
                                }
                            });

                            // Mostrar Snackbar personalizado
                            snackbar.show();

                        }
                    });

                } else {
                    //Ya esta registrado, por lo tango login
                    String url = "http://10.0.2.2:3000/loginGoogleAccount";
                    JSONObject jsonBody = new JSONObject();

                    try {
                        jsonBody.put("email", account.getEmail());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MyAsyncTaskLogin loginUser = new MyAsyncTaskLogin(url, jsonBody);
                    loginUser.execute();
                    String resultLogin = null;
                    try {
                        resultLogin = loginUser.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("infoUser", resultLogin);
                    editor.apply();
                    binding.errorLoginText.setVisibility(View.GONE);
                    startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().finish();
                }


                gsc.signOut();
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkifUsernameIsUsed(String username) {
        boolean result = false;
        String url = "http://10.0.2.2:3000/checkUserExists";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        MyAsyncTaskGetUser getUser = new MyAsyncTaskGetUser(url, jsonObject);
        getUser.execute();
        String resultGetUser = null;
        try {
            resultGetUser = getUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (resultGetUser.contains("false")){ //Si tiene false quiere decir que nadie usa ese correo. TODO OK
            result = true;
        }
        return result;
    }

    private boolean checkIfEmailIsUsed(String email) {
        boolean result = false;
        String url = "http://10.0.2.2:3000/checkEmailExists";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        MyAsyncTaskGetUser getUser = new MyAsyncTaskGetUser(url, jsonObject);
        getUser.execute();
        String resultGetUser = null;
        try {
            resultGetUser = getUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (resultGetUser.contains("false")){ //Si tiene false quiere decir que nadie usa ese correo. TODO OK
            result = true;
        }
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Obtener la referencia a la Toolbar de la MainActivity
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        BottomNavigationView bottomNavigationView= getActivity().findViewById(R.id.nav_view);

        if (bottomNavigationView != null){
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        // Mostrar la Toolbar
        if (toolbar != null) {
            toolbar.setVisibility(View.VISIBLE);
        }

        binding = null;
    }

    public void setResultLogin(Object result){
        resultLogin = result;
    }
}