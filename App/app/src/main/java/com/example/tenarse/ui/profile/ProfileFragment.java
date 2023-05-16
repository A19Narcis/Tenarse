package com.example.tenarse.ui.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentProfileBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.profile.asynctask.MyAsyncTaskFollowing;
import com.example.tenarse.ui.search.posts.MyAsyncTaskGetPosts;
import com.example.tenarse.ui.user.elements.adapters.FollowAdapter;
import com.example.tenarse.ui.user.elements.ElementUserFollow;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.elements.ListElementDoubt;
import com.example.tenarse.ui.user.elements.adapters.MultiAdapter;
import com.example.tenarse.ui.user.elements.ListElementVideo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfileFragment extends Fragment {

    private Handler mHandler = new Handler();
    private static final int DELAY_MILLIS = 500;
    List<Object> dataList;
    List<Object> usersList;
    private FragmentProfileBinding binding;

    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;

    String userInfoString;

    JSONObject userInfo;

    MultiAdapter multiAdapter;
    FollowAdapter followAdapter;

    String fragmentAnterior = "";

    private GlobalDadesUser globalDadesUser;
    private JSONObject dadesUser;

    private Runnable mRunnable = new Runnable() {
        private int mPreviousScrollPosition = -1;

        @Override
        public void run() {
            // Si el NestedScrollView sigue desplazándose después de 1 segundo, cambia a un desplazamiento rápido
            int currentScrollPosition = nestedScrollView.getScrollY();
            if (currentScrollPosition != mPreviousScrollPosition) {
                mHandler.postDelayed(this, DELAY_MILLIS);
                mPreviousScrollPosition = currentScrollPosition;
            } else {
                nestedScrollView.smoothScrollTo(0, 0);
            }
        }
    };

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dataList = new ArrayList<>();
        multiAdapter = new MultiAdapter(dataList, getContext(),ProfileFragment.this);

        usersList = new ArrayList<>();
        followAdapter = new FollowAdapter(usersList, getContext(), ProfileFragment.this);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        binding.logoHomeToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation rotateAnimation = new RotateAnimation(
                        0,
                        360,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );

                // Configura la duración de la animación y otras propiedades
                rotateAnimation.setDuration(1000);
                rotateAnimation.setFillAfter(true);

                // Inicia la animación en la ImageView
                v.startAnimation(rotateAnimation);
                nestedScrollView = binding.nestedScrollViewUser;
                nestedScrollView.smoothScrollTo(0,0);
                // Programa la ejecución del Runnable después de una demora de 1 segundo
                mHandler.postDelayed(mRunnable, DELAY_MILLIS);
            }
        });


        /* DADES DE L'USUARI SELECCIONAT */
        Bundle args = getArguments();
        if (args != null) {
            userInfoString = args.getString("userInfo");
            fragmentAnterior = args.getString("fragment");
        }

        try {
            userInfo = new JSONObject(userInfoString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
        JSONObject dadesUsuari = globalDadesUser.getDadesUser();


        binding.followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.followButton.getText().toString().equals("Seguir")){
                    //Thread per la perició de 'newFollowing'
                    String url = "http://10.0.2.2:3000/newFollowing";

                    JSONObject body = new JSONObject();

                    try {
                        body.put("user_following", dadesUsuari.getString("_id"));
                        body.put("user_followed", userInfo.getString("_id"));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    MyAsyncTaskFollowing startFollowing = new MyAsyncTaskFollowing(url, body);
                    startFollowing.execute();
                    String resultFollowing = null;
                    try {
                        resultFollowing = startFollowing.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    //Un cop acaba, canvia la vista del botó
                    binding.followButton.setBackgroundColor(Color.WHITE);
                    binding.followButton.setTextColor(Color.BLACK);
                    binding.followButton.setText("Siguiendo ✓");

                    //******* UPDATE DATOS USER **********
                    String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", dadesUsuari.getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
                    selectedUser.execute();
                    String resultSearch = null;
                    try {
                        resultSearch = selectedUser.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        JSONObject newDadesLogin = new JSONObject(resultSearch);
                        GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                        globalDadesUser.setDadesUser(newDadesLogin);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    //******* UPDATE DATOS USER **********
                    try {
                        refreshUserInfo(userInfo.getString("_id"));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(getActivity());
                    alertaLogOut.setTitle("Dejar de seguir");
                    try {
                        alertaLogOut.setMessage("¿Quieres dejar de seguir a @" + userInfo.getString("username") + "?");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    alertaLogOut.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Deixar de seguir
                            String url = "http://10.0.2.2:3000/deleteFollowing";

                            JSONObject body = new JSONObject();

                            try {
                                body.put("user_following", dadesUsuari.getString("_id"));
                                body.put("user_removed", userInfo.getString("_id"));
                            } catch (JSONException e){
                                e.printStackTrace();
                            }

                            MyAsyncTaskFollowing startFollowing = new MyAsyncTaskFollowing(url, body);
                            startFollowing.execute();
                            String resultFollowing = null;
                            try {
                                resultFollowing = startFollowing.get();
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            //Canvia el boto
                            binding.followButton.setBackgroundColor(Color.BLACK);
                            binding.followButton.setTextColor(Color.WHITE);
                            binding.followButton.setText("Seguir");


                            //******* UPDATE DATOS USER **********
                            String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("username", dadesUsuari.getString("username"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
                            selectedUser.execute();
                            String resultSearch = null;
                            try {
                                resultSearch = selectedUser.get();
                            } catch (ExecutionException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                JSONObject newDadesLogin = new JSONObject(resultSearch);
                                GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                                globalDadesUser.setDadesUser(newDadesLogin);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            //******* UPDATE DATOS USER **********
                            try {
                                refreshUserInfo(userInfo.getString("_id"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
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
            }
        });


        //Veure si ja segueixo aquest usuari per canviar el botó
        try {
            JSONArray usersSiguiendo = dadesUsuari.getJSONArray("followings");
            for (int i = 0; i < usersSiguiendo.length(); i++) {
                JSONObject user = usersSiguiendo.getJSONObject(i);
                if (user.getString("user").equals(userInfo.getString("_id"))){
                    binding.followButton.setBackgroundColor(Color.WHITE);
                    binding.followButton.setTextColor(Color.BLACK);
                    binding.followButton.setText("Siguiendo ✓");
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            if (dadesUsuari.getString("_id").equals(userInfo.getString("_id"))){
                binding.followButton.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            binding.userName.setText("@" + userInfo.getString("username"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        int numero_followings = 0;
        try {
            numero_followings = userInfo.getJSONArray("followings").length();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (numero_followings >= 10000 && numero_followings < 999950) {
            String followingsString = formatFollowers10(numero_followings);
            binding.userFolloweds.setText(followingsString); // 10.0 k
        } else if (numero_followings >= 999950){
            String followingsString = formatFollowers100(numero_followings);
            binding.userFolloweds.setText(followingsString); // 10.0 M
        } else {
            binding.userFolloweds.setText(Integer.toString(numero_followings));
        }

        int numero_followers = 0;
        try {
            numero_followers = userInfo.getJSONArray("followers").length();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        if (numero_followers >= 10000 && numero_followers < 999950) {
            String followingsString = formatFollowers10(numero_followers);
            binding.userFollowers.setText(followingsString); // 10.0 k
        } else if (numero_followers >= 999950){
            String followingsString = formatFollowers100(numero_followers);
            binding.userFollowers.setText(followingsString); // 10.0 M
        } else {
            binding.userFollowers.setText(Integer.toString(numero_followers));
        }

        try {
            Picasso.with(getContext()).invalidate(userInfo.getString("url_img"));
            Picasso.with(getContext()).load(userInfo.getString("url_img").replace("localhost", "10.0.2.2")).into(binding.fotoPerfil);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray publicacions = new JSONArray(dadesUsuari.getString("publicacions"));
            for (int i = 0; i < publicacions.length(); i++) {
                JSONObject post = publicacions.getJSONObject(i);
                //SACAR USERNAME
                String realUsername = getUsernameFromID(post);
                JSONObject readDadesUser = new JSONObject(realUsername);
                if (post.getString("tipus").equals("image")){
                    dataList.add(0, new ListElementImg(readDadesUser.getString("username"), post.getString("text"), post.getString("url_img"), post.getString("_id"), readDadesUser.getString("url_img")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video")){
                    dataList.add(0, new ListElementVideo(readDadesUser.getString("username"), post.getString("text"), post.getString("url_video"), post.getString("_id"), readDadesUser.getString("url_img")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("doubt")){
                    dataList.add(0, new ListElementDoubt(readDadesUser.getString("username"), post.getString("titol"), post.getString("text"), post.getString("_id"), readDadesUser.getString("url_img")));
                    multiAdapter.notifyItemInserted(0);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refreshUserInfo(userInfo.getString("_id"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });


        try {
            refreshUserInfo(userInfo.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        recyclerView = binding.recyclerViewFeed;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(multiAdapter);

        binding.userFollowers.setOnClickListener(v -> {

            usersList.clear();

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.seguidores_dialog);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                // Calcular el ancho deseado para el diálogo (la mitad de la pantalla)
                int desiredWidth = (int) (screenWidth / 1.45f);
                int desiredHeight = screenHeight / 3;

                // Obtener la ventana del diálogo
                Window window = dialog.getWindow();
                if (window != null) {
                    // Establecer el ancho y alto personalizados
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.copyFrom(window.getAttributes());
                    params.width = desiredWidth;
                    params.height = desiredHeight;

                    // Establecer la gravedad para centrar horizontalmente
                    params.gravity = Gravity.CENTER_HORIZONTAL;

                    window.setAttributes(params);

                    // Aplicar bordes redondeados al diálogo
                    int cornerRadius = 20; // Valor en píxeles, ajusta según tus necesidades
                    ShapeDrawable shapeDrawable = new ShapeDrawable();
                    shapeDrawable.getPaint().setColor(Color.WHITE); // Color del fondo del diálogo
                    shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable.getPaint().setAntiAlias(true);
                    shapeDrawable.setShape(new RoundRectShape(
                            new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius},
                            null,
                            null));
                    window.setBackgroundDrawable(shapeDrawable);
                }
            }

            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewSeguidores);
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            //Cargar seguidores /*IMAGEN PERFIL*/ - /*@USERNAME*/
            String url = "http://10.0.2.2:3000/getFollowersInfo";
            JSONObject body = new JSONObject();
            try {
                body.put("id_user", userInfo.getString("_id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            MyAsyncTaskGetPosts getInfoFollowers = new MyAsyncTaskGetPosts(url, body);
            getInfoFollowers.execute();
            String result = null;
            try {
                result = getInfoFollowers.get();

                JSONArray followsArray = new JSONArray(result);

                for (int i = 0; i < followsArray.length(); i++) {
                    JSONObject user = followsArray.getJSONObject(i);
                    usersList.add(new ElementUserFollow(user.getString("username"), user.getString("url_img")));
                    followAdapter.notifyItemInserted(usersList.size() - 1);
                }


                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(followAdapter);

            } catch (ExecutionException | InterruptedException | JSONException e) {
                throw new RuntimeException(e);
            }

            dialog.show();
        });

        binding.userFolloweds.setOnClickListener(v -> {
            usersList.clear();

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.seguidores_dialog);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                int screenWidth = displayMetrics.widthPixels;
                int screenHeight = displayMetrics.heightPixels;

                // Calcular el ancho deseado para el diálogo (la mitad de la pantalla)
                int desiredWidth = (int) (screenWidth / 1.45f);
                int desiredHeight = screenHeight / 3;

                // Obtener la ventana del diálogo
                Window window = dialog.getWindow();
                if (window != null) {
                    // Establecer el ancho y alto personalizados
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.copyFrom(window.getAttributes());
                    params.width = desiredWidth;
                    params.height = desiredHeight;

                    // Establecer la gravedad para centrar horizontalmente
                    params.gravity = Gravity.CENTER_HORIZONTAL;

                    window.setAttributes(params);

                    // Aplicar bordes redondeados al diálogo
                    int cornerRadius = 20; // Valor en píxeles, ajusta según tus necesidades
                    ShapeDrawable shapeDrawable = new ShapeDrawable();
                    shapeDrawable.getPaint().setColor(Color.WHITE); // Color del fondo del diálogo
                    shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
                    shapeDrawable.getPaint().setAntiAlias(true);
                    shapeDrawable.setShape(new RoundRectShape(
                            new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius},
                            null,
                            null));
                    window.setBackgroundDrawable(shapeDrawable);
                }
            }

            RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewSeguidores);
            TextView texto = dialog.findViewById(R.id.followers_window_text);
            texto.setText("Siguiendo");
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            //Cargar seguidores /*IMAGEN PERFIL*/ - /*@USERNAME*/
            String url = "http://10.0.2.2:3000/getFollowingsInfo";
            JSONObject body = new JSONObject();
            try {
                body.put("id_user", userInfo.getString("_id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            MyAsyncTaskGetPosts getInfoFollowers = new MyAsyncTaskGetPosts(url, body);
            getInfoFollowers.execute();
            String result = null;
            try {
                result = getInfoFollowers.get();

                JSONArray followsArray = new JSONArray(result);

                for (int i = 0; i < followsArray.length(); i++) {
                    JSONObject user = followsArray.getJSONObject(i);
                    usersList.add(new ElementUserFollow(user.getString("username"), user.getString("url_img")));
                    followAdapter.notifyItemInserted(usersList.size() - 1);
                }


                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(followAdapter);

            } catch (ExecutionException | InterruptedException | JSONException e) {
                throw new RuntimeException(e);
            }

            dialog.show();
        });


        return root;
    }

    public static String formatFollowers10(int num) {
        if (num >= 1000) {
            String numStr = String.valueOf((double) num / 1000);
            if (num % 1000 >= 100) {
                numStr = String.format("%.1f", (double) num / 1000);
            }
            return numStr + "k";
        } else {
            return String.valueOf(num);
        }
    }

    public static String formatFollowers100(int num) {
        if (num >= 999950) {
            String numStr = String.valueOf((double) num / 1000000);
            if (num % 1000000 >= 100000) {
                numStr = String.format("%.1f", (double) num / 1000000);
            }
            return numStr + "M";
        } else {
            return String.valueOf(num);
        }
    }

    private void refreshUserInfo(String id){
        //Actualitzar el perfil
        //******* UPDATE DATOS USER **********
        String url_selectUser = "http://10.0.2.2:3000/getUserById";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_user", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
        selectedUser.execute();
        String resultSearch = null;
        try {
            resultSearch = selectedUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject newDadesUser = new JSONObject(resultSearch);
            //Canviar els valors antics de l'usuari
            binding.userName.setText("@" + newDadesUser.getString("username"));
            Picasso.with(getContext()).invalidate(newDadesUser.getString("url_img"));
            Picasso.with(getContext()).load(newDadesUser.getString("url_img").replace("localhost", "10.0.2.2")).into(binding.fotoPerfil);
            int new_numero_followings = newDadesUser.getJSONArray("followings").length();
            if (new_numero_followings >= 10000 && new_numero_followings < 999950) {
                String followingsString = formatFollowers10(new_numero_followings);
                binding.userFolloweds.setText(followingsString); // 10.0 k
            } else if (new_numero_followings >= 999950){
                String followingsString = formatFollowers100(new_numero_followings);
                binding.userFolloweds.setText(followingsString); // 10.0 M
            } else {
                binding.userFolloweds.setText(Integer.toString(new_numero_followings));
            }

            int new_numero_followers = newDadesUser.getJSONArray("followers").length();
            if (new_numero_followers >= 10000 && new_numero_followers < 999950) {
                String followingsString = formatFollowers10(new_numero_followers);
                binding.userFollowers.setText(followingsString); // 10.0 k
            } else if (new_numero_followers >= 999950){
                String followingsString = formatFollowers100(new_numero_followers);
                binding.userFollowers.setText(followingsString); // 10.0 M
            } else {
                binding.userFollowers.setText(Integer.toString(new_numero_followers));
            }

            try {
                JSONArray usersSiguiendo = newDadesUser.getJSONArray("followings");
                for (int i = 0; i < usersSiguiendo.length(); i++) {
                    JSONObject user = usersSiguiendo.getJSONObject(i);
                    if (user.getString("user").equals(userInfo.getString("_id"))){
                        binding.followButton.setBackgroundColor(Color.WHITE);
                        binding.followButton.setTextColor(Color.BLACK);
                        binding.followButton.setText("Siguiendo ✓");
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



            JSONArray new_publicacions = new JSONArray(newDadesUser.getString("publicacions"));
            List<Object> new_dataList = new ArrayList<>();
            MultiAdapter newMultiAdapter = new MultiAdapter(new_dataList, getContext(), ProfileFragment.this);

            for (int i = 0; i < new_publicacions.length(); i++) {
                JSONObject post = new_publicacions.getJSONObject(i);
                //SACAR USERNAME & URL IMG
                String realUsername = getUsernameFromID(post);
                JSONObject realDades = new JSONObject(realUsername);
                if (post.getString("tipus").equals("image")){
                    new_dataList.add(0, new ListElementImg(realDades.getString("username"), post.getString("text"), post.getString("url_img"), post.getString("_id"), realDades.getString("url_img")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video")){
                    new_dataList.add(0, new ListElementVideo(realDades.getString("username"), post.getString("text"), post.getString("url_video"), post.getString("_id"), realDades.getString("url_img")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("doubt")){
                    new_dataList.add(0, new ListElementDoubt(realDades.getString("username"), post.getString("titol"), post.getString("text"), post.getString("_id"), realDades.getString("url_img")));
                    multiAdapter.notifyItemInserted(0);
                }
            }


            multiAdapter.setList(new_dataList);
            recyclerView = binding.recyclerViewFeed;

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerView.setAdapter(newMultiAdapter);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        //******* UPDATE DATOS USER **********
    }

    private String getUsernameFromID(JSONObject post) {
        String url_selectUser = "http://10.0.2.2:3000/getUsernameAndImageFromID";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_user", post.getString("owner"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
        selectedUser.execute();
        String resultSearch = null;
        try {
            resultSearch = selectedUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resultSearch;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void selectPost(String post_img_id, View v, String username, String url_img) {
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();

        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://10.0.2.2:3000/getSelectedPost/" + post_img_id;
        MyAsyncTaskGetSinglePost getSinglePost = new MyAsyncTaskGetSinglePost(url_selectPost);
        getSinglePost.execute();
        String resultSinglePost = null;
        try {
            resultSinglePost = getSinglePost.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Ver si el post que hemos seleccionado tiene mi `Like`
        boolean myLike = false;
        try {
            JSONObject dadesPostResult = new JSONObject(resultSinglePost);
            for (int i = 0; i < dadesPostResult.getJSONArray("likes").length() && !myLike; i++) {
                if (dadesPostResult.getJSONArray("likes").get(i).equals(dadesUser.getString("_id"))){
                    myLike = true;
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        viewSelectedPost(resultSinglePost, myLike, v, username, url_img);
    }

    public void viewSelectedPost(String infoPost, boolean myLike, View v, String username, String url_img) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "otherUser");
        bundle.putSerializable("isLiked", myLike);
        bundle.putSerializable("usernamePost", username);
        bundle.putSerializable("url_img", url_img);
        Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_viewPostFragment, bundle);
    }
}