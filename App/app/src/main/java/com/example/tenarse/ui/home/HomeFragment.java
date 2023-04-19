package com.example.tenarse.ui.home;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentHomeBinding;
import com.example.tenarse.ui.home.adapters.MultiAdapter;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.google.android.gms.common.util.JsonUtils;
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
        multiAdapter = new MultiAdapter(dataList, getContext());

        recyclerView = binding.rvHome;

        //chechIfNewPost();


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
                layoutManager.smoothScrollToPosition(recyclerView, new RecyclerView.State(), 0);

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
                    dataList.add(0, new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("user_img")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("doubt")){
                    dataList.add(0, new ListElementDoubt(post.getString("owner"), post.getString("titol"), post.getString("text"),  post.getString("user_img")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video1")){
                    dataList.add(0, new ListElementVideo(post.getString("owner"), post.getString("user_img"), post.getString("url_video"), post.getString("text")));
                    multiAdapter.notifyItemInserted(0);
                }
            }

            /*dataList.add(new ListElementImg("_A19Narcis_", ""));
            dataList.add(new ListElementDoubt("Xx_tEo_xX", "Duda real", "Como voy a la pagina web desde un socket en NodeJS?"));
            dataList.add(new ListElementImg("_A19Narcis_", "Este es un ejemplo de post de imagen con texto"));*/

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
}