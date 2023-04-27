package com.example.tenarse.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentHomeBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.adapters.MultiAdapter;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskHomePosts;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskLikes;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.example.tenarse.ui.post.ViewPostFragment;
import com.example.tenarse.ui.profile.ProfileFragment;
import com.example.tenarse.ui.search.users.ListElementUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // Establece una demora de 1 segundo antes de cancelar la animación de desplazamiento suave
    private static final int DELAY_MILLIS = 500;
    private static final String URL = "http://10.0.2.2:3000/getPosts";

    private Handler mHandler = new Handler();

    private FragmentHomeBinding binding;
    private boolean shouldReloadOnBackPressed = false;
    private ScrollView scrollView;

    /* Multi ADAPTER */
    List<Object> dataList;
    MultiAdapter multiAdapter;


    RecyclerView recyclerView;

    private Object resultNewPosts;
    private int resultLengthPost;

    private GlobalDadesUser globalDadesUser;
    private JSONObject dadesUser;

    private boolean isLiked;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // Si la RecyclerView sigue desplazándose suavemente después de 1 segundo, cambia a un desplazamiento rápido
            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity mainActivity = (MainActivity) getActivity();

        dataList = new ArrayList<>();
        multiAdapter = new MultiAdapter(dataList, getContext(), HomeFragment.this);

        recyclerView = binding.rvHome;

        globalDadesUser = GlobalDadesUser.getInstance();

        if (dadesUser == null){
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String dadesUserMyPrefs = sharedPreferences.getString("infoUser", "");

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(dadesUserMyPrefs);
                globalDadesUser.setDadesUser(jsonObject);
                dadesUser = globalDadesUser.getDadesUser();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }


         chechIfNewPost();

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
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (recyclerView != null){
                    layoutManager.smoothScrollToPosition(recyclerView, new RecyclerView.State(), 0);
                }
                // Programa la ejecución del Runnable después de una demora de 1 segundo
                mHandler.postDelayed(mRunnable, DELAY_MILLIS);
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    //chechIfNewPost();
                    binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        ImageView nots = binding.notificacionesImg;

        nots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_navigation_nots);
            }
        });

        return root;
    }

    private void chechIfNewPost() {
        MyAsyncTaskHomePosts getPosts = new MyAsyncTaskHomePosts(URL);
        getPosts.execute();
        String resultGetPosts = null;
        try {
            resultGetPosts = getPosts.get();
            JSONArray jsonArray = new JSONArray(resultGetPosts);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject post = jsonArray.getJSONObject(i);
                if (post.getString("tipus").equals("image")){
                    isLiked = false;
                    ListElementImg listElementImg = new ListElementImg(post.getString("_id"), post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("user_img"), post.getJSONArray("likes"));
                    for (int j = 0; j < listElementImg.getLikes().length() && !isLiked; j++) {
                        if (listElementImg.getLikes().get(j).toString().equals(dadesUser.getString("_id"))) {
                            isLiked = true;
                            listElementImg.setLiked(true);
                        }
                    }
                    dataList.add(0, listElementImg);
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("doubt")){
                    isLiked = false;
                    ListElementDoubt listElementDoubt = new ListElementDoubt(post.getString("_id"), post.getString("owner"), post.getString("titol"), post.getString("text"),  post.getString("user_img"), post.getJSONArray("likes"));
                    listElementDoubt.setLiked(false);
                    for (int j = 0; j < listElementDoubt.getLikes().length() && !isLiked; j++) {
                        if (listElementDoubt.getLikes().get(j).toString().equals(dadesUser.getString("_id"))){
                            isLiked = true;
                            listElementDoubt.setLiked(true);
                        }
                    }
                    dataList.add(0, listElementDoubt);
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video1")){
                    dataList.add(0, new ListElementVideo(post.getString("_id"), post.getString("owner"), post.getString("user_img"), post.getString("url_video"), post.getString("text")));
                    multiAdapter.notifyItemInserted(0);
                }
            }


            ListElementVideo listElementVideo = new ListElementVideo("aaaaa", "user", "", "",  "Prueba de video");
            dataList.add(listElementVideo);
            multiAdapter.notifyItemInserted(0);


            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(multiAdapter);



        } catch (ExecutionException | InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setRefreshing(false);
    }

    public void loadNewPosts(Object nuevosPostsBD){
        resultNewPosts = nuevosPostsBD;
    }

    public void selectUser(String username){
        //Recoger todos los datos del usuario que tiene ese `username` y luego cambiar de fragment para ver su perfil
        String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("username", username);
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
            JSONObject dadesLogin = new JSONObject(resultSearch);
            ListElementUser userSelected = new ListElementUser(dadesLogin.getString("_id"), dadesLogin.getString("url_img"), dadesLogin.getString("username"), dadesLogin.getString("nombre") + " " + dadesLogin.getString("apellidos"), dadesLogin.getJSONArray("followers").length(), dadesLogin.getJSONArray("followings").length(), dadesLogin.getJSONArray("publicacions"));
            viewSelectedUser(userSelected);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewSelectedUser(ListElementUser userSelected) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userInfo", userSelected);
        profileFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.viewFragment, profileFragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void selectPost(String idPost){
        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://10.0.2.2:3000/getSelectedPost/" + idPost;
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

        viewSelectedPost(resultSinglePost, myLike);

    }

    public void viewSelectedPost(String infoPost, boolean myLike) {
        //Carregar el nou fragment amb les seves dades
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "home");
        bundle.putSerializable("isLiked", myLike);
        transaction.replace(R.id.viewFragment, viewPostFragment);
        viewPostFragment.setArguments(bundle);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();
    }



    public void addLike(String idPost) {
        //Thread para dar like
        dadesUser = globalDadesUser.getDadesUser();
        isLiked = true;
        String url = "http://10.0.2.2:3000/newLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", idPost);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskLikes likesTask = new MyAsyncTaskLikes(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLike(String idPost) {
        //Quitar like
        dadesUser = globalDadesUser.getDadesUser();
        isLiked = false;
        String url = "http://10.0.2.2:3000/removeLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", idPost);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskLikes likesTask = new MyAsyncTaskLikes(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}