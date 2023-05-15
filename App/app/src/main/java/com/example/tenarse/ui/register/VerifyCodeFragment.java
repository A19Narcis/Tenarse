package com.example.tenarse.ui.register;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.example.tenarse.Login;
import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentVerifyCodeBinding;
import com.example.tenarse.globals.MyAsyncTask;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class VerifyCodeFragment extends Fragment {

    private FragmentVerifyCodeBinding binding;
    private String mensaje;
    private String code;
    Semaphore semaphore = new Semaphore(0);
    private JSONObject finalDadesUser;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3001");

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVerifyCodeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            try {
                finalDadesUser = new JSONObject(args.getString("dadesUserJSON"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        verifyEmailWithCode(finalDadesUser);

        binding.backButton.setOnClickListener(view -> {
            Navigation.findNavController(view).popBackStack();
        });

        binding.verifyButton.setOnClickListener(view -> {
            if (code.equals(binding.firstPinView.getText().toString())){
                registerUserVerified();
            } else {
                ObjectAnimator animator = ObjectAnimator.ofFloat(binding.firstPinView, "translationX", 0f, 10f, -10f, 0f);
                animator.setDuration(200);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.start();
            }
        });

        return root;
    }

    private void verifyEmailWithCode(JSONObject finalDadesUser) {
        String url = "http:10.0.2.2:3000/verifyEmail";

        JSONObject bodyEmailVerification = new JSONObject();
        try {
            bodyEmailVerification.put("email", finalDadesUser.getString("email"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTask getVerifyResult = new MyAsyncTask(url, bodyEmailVerification);
        getVerifyResult.execute();
        String resultCode = null;
        try {
            resultCode = getVerifyResult.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        code = resultCode;
    }

    private void registerUserVerified() {
        try {
            mSocket.connect();
            mSocket.emit("addNewUser", finalDadesUser);

            // Espera hasta que se libere el semáforo (es decir, hasta que se reciba la respuesta)
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        acabarRegistro(mensaje);
    }

    private void acabarRegistro(String resultRegister) {
        if (!resultRegister.contains("false")){
            binding.userExisteRegister.setVisibility(View.GONE);
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
                        body.put("username", finalDadesUser.getString("username"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    MyAsyncTask getUser = new MyAsyncTask(urlGetUser, body);
                    getUser.execute();
                    String resultGetUserRegistered = null;
                    try {
                        resultGetUserRegistered = getUser.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

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

        } else {
            //Este usuario ya existe
            binding.userExisteRegister.setVisibility(View.VISIBLE);
        }
    }
}