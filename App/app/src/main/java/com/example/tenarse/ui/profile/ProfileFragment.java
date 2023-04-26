package com.example.tenarse.ui.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.Login;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentProfileBinding;
import com.example.tenarse.databinding.FragmentUserBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.post.ViewPostFragment;
import com.example.tenarse.ui.profile.asynctask.MyAsyncTaskFollowing;
import com.example.tenarse.ui.search.SearchFragment;
import com.example.tenarse.ui.search.users.ListElementUser;
import com.example.tenarse.ui.user.UserFragment;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.elements.ListElementDoubt;
import com.example.tenarse.ui.user.adapters.MultiAdapter;
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
    private FragmentProfileBinding binding;

    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;

    ListElementUser userInfo;

    MultiAdapter multiAdapter;

    String fragmentAnterior = "";

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
        multiAdapter = new MultiAdapter(dataList, ProfileFragment.this);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
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
            userInfo = (ListElementUser) args.getSerializable("userInfo");
            fragmentAnterior = args.getString("fragment");
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
                        body.put("user_following", dadesUsuari.getString("username"));
                        body.put("user_followed", userInfo.getSearch_username());
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    System.out.println(body);

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

                    //Pujar un seguidor per veure els canvis
                    int numero_followers = userInfo.getFollowers_search() + 1;
                    if (numero_followers >= 10000 && numero_followers < 999950) {
                        String followingsString = formatFollowers10(numero_followers);
                        binding.userFollowers.setText(followingsString); // 10.0 k
                    } else if (numero_followers >= 999950){
                        String followingsString = formatFollowers100(numero_followers);
                        binding.userFollowers.setText(followingsString); // 10.0 M
                    } else {
                        binding.userFollowers.setText(Integer.toString(numero_followers));
                    }

                    //******* UPDATE DATOS USER **********
                    String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", dadesUsuari.getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MyAsyncTaskGetUser selectedUser = new MyAsyncTaskGetUser(url_selectUser, jsonBody);
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
                } else {
                    AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(getActivity());
                    alertaLogOut.setTitle("Dejar de seguir");
                    alertaLogOut.setMessage("¿Quieres dejar de seguir a @" + userInfo.getSearch_username() + "?");
                    alertaLogOut.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Deixar de seguir
                            String url = "http://10.0.2.2:3000/deleteFollowing";

                            JSONObject body = new JSONObject();

                            try {
                                body.put("user_following", dadesUsuari.getString("username"));
                                body.put("user_removed", userInfo.getSearch_username());
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

                            //Baixar un seguidor per veure els canvis
                            int numero_followers = userInfo.getFollowers_search();
                            if (numero_followers >= 10000 && numero_followers < 999950) {
                                String followingsString = formatFollowers10(numero_followers);
                                binding.userFollowers.setText(followingsString); // 10.0 k
                            } else if (numero_followers >= 999950){
                                String followingsString = formatFollowers100(numero_followers);
                                binding.userFollowers.setText(followingsString); // 10.0 M
                            } else {
                                binding.userFollowers.setText(Integer.toString(numero_followers));
                            }

                            //******* UPDATE DATOS USER **********
                            String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
                            JSONObject jsonBody = new JSONObject();
                            try {
                                jsonBody.put("username", dadesUsuari.getString("username"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MyAsyncTaskGetUser selectedUser = new MyAsyncTaskGetUser(url_selectUser, jsonBody);
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
                if (user.getString("user").equals(userInfo.getSearch_username())){
                    binding.followButton.setBackgroundColor(Color.WHITE);
                    binding.followButton.setTextColor(Color.BLACK);
                    binding.followButton.setText("Siguiendo ✓");
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            if (dadesUsuari.getString("username").equals(userInfo.getSearch_username())){
                binding.followButton.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.userName.setText("@" + userInfo.getSearch_username());
        int numero_followings = userInfo.getFollowing_search();
        if (numero_followings >= 10000 && numero_followings < 999950) {
            String followingsString = formatFollowers10(numero_followings);
            binding.userFolloweds.setText(followingsString); // 10.0 k
        } else if (numero_followings >= 999950){
            String followingsString = formatFollowers100(numero_followings);
            binding.userFolloweds.setText(followingsString); // 10.0 M
        } else {
            binding.userFolloweds.setText(Integer.toString(numero_followings));
        }

        int numero_followers = userInfo.getFollowers_search();
        if (numero_followers >= 10000 && numero_followers < 999950) {
            String followingsString = formatFollowers10(numero_followers);
            binding.userFollowers.setText(followingsString); // 10.0 k
        } else if (numero_followers >= 999950){
            String followingsString = formatFollowers100(numero_followers);
            binding.userFollowers.setText(followingsString); // 10.0 M
        } else {
            binding.userFollowers.setText(Integer.toString(numero_followers));
        }


        Picasso.with(getContext()).load(userInfo.getUser_url_img().replace("localhost", "10.0.2.2")).into(binding.fotoPerfil);


        try {
            JSONArray publicacions = userInfo.getPublicacions_search();
            for (int i = 0; i < publicacions.length(); i++) {
                JSONObject post = publicacions.getJSONObject(i);
                if (post.getString("tipus").equals("image")){
                    dataList.add(new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("_id")));
                } else if (post.getString("tipus").equals("video")){
                    //Añadir video
                } else if (post.getString("tipus").equals("doubt")){
                    dataList.add(new ListElementDoubt(post.getString("owner"), post.getString("titol"), post.getString("text"), post.getString("_id")));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUserInfo(userInfo.getId_user());
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = binding.recyclerViewFeed;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(multiAdapter);

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
        MyAsyncTaskGetUser selectedUser = new MyAsyncTaskGetUser(url_selectUser, jsonBody);
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


            JSONArray new_publicacions = new JSONArray(newDadesUser.getString("publicacions"));
            List<Object> new_dataList = new ArrayList<>();

            for (int i = 0; i < new_publicacions.length(); i++) {
                JSONObject post = new_publicacions.getJSONObject(i);
                if (post.getString("tipus").equals("image")){
                    new_dataList.add(new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("_id")));
                } else if (post.getString("tipus").equals("video")){
                    //Añadir video
                } else if (post.getString("tipus").equals("doubt")){
                    new_dataList.add(new ListElementDoubt(post.getString("owner"), post.getString("titol"), post.getString("text"), post.getString("_id")));
                }
            }

            MultiAdapter newMultiAdapter = new MultiAdapter(new_dataList, ProfileFragment.this);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void selectPost(String post_img_id) {
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

        viewSelectedPost(resultSinglePost);
    }

    public void viewSelectedPost(String infoPost) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "otherUser");
        transaction.replace(R.id.viewFragment, viewPostFragment);
        viewPostFragment.setArguments(bundle);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}