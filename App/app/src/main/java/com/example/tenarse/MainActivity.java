package com.example.tenarse;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.search.SearchFragment;
import com.example.tenarse.ui.threads.LoadImageBottomNavBar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.tenarse.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Objects;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public View mobileNavigation;

    public Toolbar toolbar;

    public boolean isLogged = false;

    BottomNavigationView navView;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

    public Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:3001");

            mSocket.on("listenChats", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Modificar las vistas de la interfaz de usuario aquí
                            changeChatIconNot();
                        }
                    });
                }
            });
        } catch (URISyntaxException e) {}
    }

    private void changeChatIconNot() {
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navigation_message);
        menuItem.setIcon(R.drawable.unsel_chat_not);
    }

    public void changeChatIconNormal() {
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navigation_message);
        menuItem.setIcon(R.drawable.msg_icon);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();



        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);

        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        navView.setItemBackgroundResource(android.R.color.transparent);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_add, R.id.navigation_message, R.id.navigation_user)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.viewFragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mobileNavigation = findViewById(R.id.mobile_navigation);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String lastActivity = sharedPreferences.getString("infoUser", "");

        try {
            GlobalDadesUser.getInstance().setDadesUser(new JSONObject(lastActivity));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            if (dadesUsuari == null || dadesUsuari.toString().equals("{}")){
                JSONObject jsonObject = new JSONObject(lastActivity);
                globalDadesUser.setDadesUser(jsonObject);
                dadesUsuari = globalDadesUser.getDadesUser();
                Menu menu = navView.getMenu();
                MenuItem menuItem = menu.findItem(R.id.navigation_user);
                LoadImageBottomNavBar loadImageBottomNavBar = new LoadImageBottomNavBar(menuItem, this);
                try {
                    loadImageBottomNavBar.execute(dadesUsuari.getString("url_img").replace("localhost", "10.0.2.2"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                if (!dadesUsuari.getString("username").equals("false")){
                    Menu menu = navView.getMenu();
                    MenuItem menuItem = menu.findItem(R.id.navigation_user);
                    globalDadesUser.setDadesUser(new JSONObject(lastActivity));
                    dadesUsuari = globalDadesUser.getDadesUser();
                    LoadImageBottomNavBar loadImageBottomNavBar = new LoadImageBottomNavBar(menuItem, this);
                    try {
                        loadImageBottomNavBar.execute(dadesUsuari.getString("url_img").replace("localhost", "10.0.2.2"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mSocket.connect();
        try {
            mSocket.emit("updateSocket", dadesUsuari.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateUserImageBottom(){
        JSONObject newDadesUsuari = globalDadesUser.getDadesUser();
        Menu menu = navView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.navigation_user);
        LoadImageBottomNavBar loadImageBottomNavBar = new LoadImageBottomNavBar(menuItem, this);
        try {
            loadImageBottomNavBar.execute(newDadesUsuari.getString("url_img").replace("localhost", "10.0.2.2"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        // Guardar el nombre de la actividad actual en la preferencia compartida
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastActivity", "Menu");
        GlobalDadesUser globalDadesExit = GlobalDadesUser.getInstance();
        JSONObject dadesUsuariExit = globalDadesExit.getDadesUser();
        System.out.println("SALGO DADES USER: " + globalDadesExit.getDadesUser().toString());
        editor.putString("infoUser", dadesUsuariExit.toString());
        editor.apply();
    }

    public Socket getmSocket() {
        return mSocket;
    }
}