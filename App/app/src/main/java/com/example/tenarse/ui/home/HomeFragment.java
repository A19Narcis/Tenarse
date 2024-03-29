package com.example.tenarse.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskHomePosts;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
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

    private static int numPagina = 0;
    private static String url = "http://212.227.40.235:3000/getPosts/" + numPagina;

    private Handler mHandler = new Handler();

    private FragmentHomeBinding binding;
    private boolean shouldReloadOnBackPressed = false;
    private ScrollView scrollView;

    /* Multi ADAPTER */
    List<Object> dataList;
    MultiAdapter multiAdapter;

    private static final String KEY_DATA_LIST = "dataList";
    private static final String KEY_RECYCLER_POSITION = "recyclerPosition";

    private Parcelable recyclerPosition;

    RecyclerView recyclerView;

    private Object resultNewPosts;
    private int resultLengthPost;

    private GlobalDadesUser globalDadesUser;
    private JSONObject dadesUser;

    private boolean isLiked;

    private boolean isFirstEntry;

    private ProgressBar progressBar;

    private Parcelable recyclerViewState;

    private int posicionPosts = 0;
    private boolean noMorePosts;

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

        recyclerView = binding.rvHome;
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        dataList = new ArrayList<>();
        multiAdapter = new MultiAdapter(dataList, getContext(), HomeFragment.this, mainActivity);

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

        if (globalDadesUser.isFirstEntry()){
            checkIfNewPost();
            globalDadesUser.setFirstEntry(false);
        } else {
            dataList = globalDadesUser.getDataList();

            if (dataList.size() > 0) {
                multiAdapter.setDataList(dataList);
                multiAdapter.notifyDataSetChanged();
            } else {
                // dataList está vacía, vuelve a cargar los datos
                checkIfNewPost();
            }
        }

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
                    checkIfNewPostReload();
                    binding.swipeRefreshLayout.setRefreshing(false);
            }
        });


        recyclerView.setSaveEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(multiAdapter);

        return root;
    }

    private void checkIfNewPostReload() {
        numPagina = 0;
        url = "http://212.227.40.235:3000/getPosts/" + numPagina;
        dataList.clear();
        checkIfNewPost();
        multiAdapter.notifyDataSetChanged();
    }

    private void checkIfNewPost() {
        MyAsyncTaskHomePosts getPosts = new MyAsyncTaskHomePosts(url);
        getPosts.execute();
        String resultGetPosts = null;
        try {
            resultGetPosts = getPosts.get();
            JSONArray jsonArray = new JSONArray(resultGetPosts);
            if (jsonArray.length() < 5){
                noMorePosts = true;
            } else {
                noMorePosts = false;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject post = jsonArray.getJSONObject(i);
                //El owner llega como una ID, con esta ID sacamos el username
                //SACAR USERNAME & IMAGE URL
                String realUsername = getUsernameandImageFromID(post);
                JSONObject username_image = new JSONObject(realUsername);
                if (post.getString("tipus").equals("image")){
                    isLiked = false;
                    ListElementImg listElementImg = new ListElementImg(post.getString("_id"), username_image.getString("username"), post.getString("text"), post.getString("url_img"), username_image.getString("url_img").replace("https://tenarse.online", "http://212.227.40.235"), post.getJSONArray("likes"), post.getString("owner"));
                    for (int j = 0; j < listElementImg.getLikes().length() && !isLiked; j++) {
                        if (listElementImg.getLikes().get(j).toString().equals(dadesUser.getString("_id"))) {
                            isLiked = true;
                            listElementImg.setLiked(true);
                        }
                    }
                    dataList.add(listElementImg);
                    multiAdapter.notifyItemInserted(dataList.size() - 1);
                } else if (post.getString("tipus").equals("doubt")){
                    isLiked = false;
                    ListElementDoubt listElementDoubt = new ListElementDoubt(post.getString("_id"), username_image.getString("username"), post.getString("titol"), post.getString("text"),  username_image.getString("url_img"), post.getJSONArray("likes"), post.getString("owner"));
                    listElementDoubt.setLiked(false);
                    System.out.println("USERNAME_IMAGE: " + username_image.toString());
                    System.out.println("POST: " + listElementDoubt.toString());
                    for (int j = 0; j < listElementDoubt.getLikes().length() && !isLiked; j++) {
                        if (listElementDoubt.getLikes().get(j).toString().equals(dadesUser.getString("_id"))){
                            isLiked = true;
                            listElementDoubt.setLiked(true);
                        }
                    }
                    dataList.add(listElementDoubt);
                    multiAdapter.notifyItemInserted(dataList.size() - 1);
                } else if (post.getString("tipus").equals("video")){
                    isLiked = false;
                    ListElementVideo listElementVideo = new ListElementVideo(post.getString("_id"), username_image.getString("username"), username_image.getString("url_img"), post.getString("url_video"), post.getString("text"), post.getJSONArray("likes"), post.getString("owner"));
                    listElementVideo.setLiked(false);
                    for (int j = 0; j < listElementVideo.getLikes().length() && !isLiked; j++) {
                        if (listElementVideo.getLikes().get(j).toString().equals(dadesUser.getString("_id"))){
                            isLiked = true;
                            listElementVideo.setLiked(true);
                        }
                    }
                    dataList.add(listElementVideo);
                    multiAdapter.notifyItemInserted(dataList.size() - 1);
                }
            }

        } catch (ExecutionException | InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private String getUsernameandImageFromID(JSONObject post) {
        String url_selectUser = "http://212.227.40.235:3000/getUsernameAndImageFromID";
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

    @Override
    public void onRefresh() {
        SwipeRefreshLayout swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setRefreshing(false);
    }

    public void loadNewPosts(Object nuevosPostsBD){
        resultNewPosts = nuevosPostsBD;
    }

    public void selectUser(String id_user, View view){
        //Recoger todos los datos del usuario que tiene ese `username` y luego cambiar de fragment para ver su perfil
        String url_selectUser = "http://212.227.40.235:3000/getUserById";
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("id_user", id_user);
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
            JSONObject dadesUser = new JSONObject(resultSearch);
            viewSelectedUser(dadesUser, view);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewSelectedUser(JSONObject dadesUser, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("userInfo", dadesUser.toString());
        Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_profileFragment, bundle);
    }

    public void selectPost(String idPost, View view, String username, String url_img){
        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://212.227.40.235:3000/getSelectedPost/" + idPost;
        MyAsyncTaskGetSinglePost getSinglePost = new MyAsyncTaskGetSinglePost(url_selectPost);
        getSinglePost.execute();
        String resultSinglePost = null;
        try {
            resultSinglePost = getSinglePost.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("RESULT: " + resultSinglePost);

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

        viewSelectedPost(resultSinglePost, myLike, view, username, url_img);

    }

    public void viewSelectedPost(String infoPost, boolean myLike, View view, String username, String url_img) {
        //Carregar el nou fragment amb les seves dades
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "home");
        bundle.putSerializable("isLiked", myLike);
        bundle.putSerializable("usernamePost", username);
        bundle.putSerializable("url_img", url_img);
        Navigation.findNavController(view).navigate(R.id.action_navigation_home_to_viewPostFragment, bundle);
    }



    public void addLike(String idPost) {
        //Thread para dar like
        dadesUser = globalDadesUser.getDadesUser();
        isLiked = true;
        String url = "http://212.227.40.235:3000/newLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", idPost);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTask likesTask = new MyAsyncTask(url, body);
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
        String url = "http://212.227.40.235:3000/removeLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", idPost);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTask likesTask = new MyAsyncTask(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void morePosts() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (!noMorePosts){
                    numPagina++;
                    url = "http://212.227.40.235:3000/getPosts/" + numPagina;
                    checkIfNewPost();
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        globalDadesUser.setDataList(dataList);
    }

}