package com.example.tenarse.ui.profile;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentProfileBinding;
import com.example.tenarse.databinding.FragmentUserBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.search.users.ListElementUser;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.elements.ListElementDoubt;
import com.example.tenarse.ui.user.adapters.MultiAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private Handler mHandler = new Handler();
    private static final int DELAY_MILLIS = 500;
    List<Object> dataList;
    private FragmentProfileBinding binding;

    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;

    ListElementUser userInfo;

    MultiAdapter multiAdapter;

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
        multiAdapter = new MultiAdapter(dataList);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUserInfo();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        /* DADES DE L'USUARI SELECCIONAT */
        Bundle args = getArguments();
        if (args != null) {
            userInfo = (ListElementUser) args.getSerializable("userInfo");
            System.out.println("USER: " + userInfo.toString());
        }


        /*GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
        JSONObject dadesUsuari = globalDadesUser.getDadesUser();*/

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
                    dataList.add(new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img")));
                } else if (post.getString("tipus").equals("video")){
                    //Añadir video
                } else if (post.getString("tipus").equals("doubt")){
                    dataList.add(new ListElementDoubt(post.getString("owner"), post.getString("titol"), post.getString("text")));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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

    private void refreshUserInfo(){
        Toast.makeText(getContext(), "New info", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}