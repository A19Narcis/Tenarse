package com.example.tenarse;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tenarse.databinding.FragmentUserBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.threads.LoadImageBottomNavBar;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.login.LoginFragment;
import com.example.tenarse.ui.notificaciones.NotificacionesFragment;
import com.example.tenarse.ui.user.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
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

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public View mobileNavigation;

    public Toolbar toolbar;

    public boolean isLogged = false;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);

        BottomNavigationView navView = findViewById(R.id.nav_view);
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
            if (dadesUsuari == null){
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Guardar el nombre de la actividad actual en la preferencia compartida
        /*SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastActivity", "Menu");
        editor.putString("infoUser", dadesUsuari.toString());
        editor.apply();*/
    }
}