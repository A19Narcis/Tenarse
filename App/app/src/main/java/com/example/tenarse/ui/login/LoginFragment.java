package com.example.tenarse.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;

public class LoginFragment extends Fragment {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button googleBtn;
    private Button loginBtn;

    TextView registrarseBtn;

    TextView test;

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
                    System.out.println(jsonBody);
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

                if (resultLogin.contains("true") || (email_username.equals("") && passwd.equals(""))){
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
                System.out.println("REGISTER CLIKED");
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(), gso);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(getContext());

        if (googleSignInAccount != null){

        }


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
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
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