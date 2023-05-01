package com.example.tenarse.ui.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentUserBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.user.elements.ListElementVideo;
import com.example.tenarse.ui.post.ViewPostFragment;
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

public class UserFragment extends Fragment {

    private Handler mHandler = new Handler();
    private static final int DELAY_MILLIS = 500;
    List<Object> dataList;
    private FragmentUserBinding binding;

    RecyclerView recyclerView;
    NestedScrollView nestedScrollView;

    MultiAdapter multiAdapter;

    private GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    private JSONObject dadesUsuari = globalDadesUser.getDadesUser();

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
        UserViewModel userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        dataList = new ArrayList<>();
        multiAdapter = new MultiAdapter(dataList, getContext(), UserFragment.this);
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        try {
            refreshUserInfo(dadesUsuari.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
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
                nestedScrollView = binding.nestedScrollViewUser;
                nestedScrollView.smoothScrollTo(0,0);
                // Programa la ejecución del Runnable después de una demora de 1 segundo
                mHandler.postDelayed(mRunnable, DELAY_MILLIS);
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refreshUserInfo(dadesUsuari.getString("_id"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });


        try {
            binding.userName.setText("@" + dadesUsuari.getString("username"));
            JSONArray followings = new JSONArray(dadesUsuari.getString("followings"));
            int numero_followings = followings.length();
            if (numero_followings >= 10000 && numero_followings < 999950) {
                String followingsString = formatFollowers10(numero_followings);
                binding.userFolloweds.setText(followingsString); // 10.0 k
            } else if (numero_followings >= 999950){
                String followingsString = formatFollowers100(numero_followings);
                binding.userFolloweds.setText(followingsString); // 10.0 M
            } else {
                binding.userFolloweds.setText(Integer.toString(numero_followings));
            }

            JSONArray followers = new JSONArray(dadesUsuari.getString("followers"));
            int numero_followers = followers.length();
            if (numero_followers >= 10000 && numero_followers < 999950) {
                String followingsString = formatFollowers10(numero_followers);
                binding.userFollowers.setText(followingsString); // 10.0 k
            } else if (numero_followers >= 999950){
                String followingsString = formatFollowers100(numero_followers);
                binding.userFollowers.setText(followingsString); // 10.0 M
            } else {
                binding.userFollowers.setText(Integer.toString(numero_followers));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            if (!dadesUsuari.getString("username").equals("false")){
                Picasso.with(getContext()).load(dadesUsuari.getString("url_img").replace("localhost", "10.0.2.2")).into(binding.fotoPerfil);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_user_to_navigation_settings);
            }
        });

        try {
            JSONArray publicacions = new JSONArray(dadesUsuari.getString("publicacions"));
            for (int i = 0; i < publicacions.length(); i++) {
                JSONObject post = publicacions.getJSONObject(i);
                if (post.getString("tipus").equals("image")){
                    dataList.add(0, new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("_id")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video")){
                    dataList.add(0, new ListElementVideo(post.getString("owner"), post.getString("text"), post.getString("url_video"), post.getString("_id")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("doubt")){
                    dataList.add(0, new ListElementDoubt(post.getString("owner"), post.getString("titol"), post.getString("text"), post.getString("_id")));
                    multiAdapter.notifyItemInserted(0);
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
            MultiAdapter newMultiAdapter = new MultiAdapter(new_dataList, getContext(), UserFragment.this);

            for (int i = 0; i < new_publicacions.length(); i++) {
                JSONObject post = new_publicacions.getJSONObject(i);
                if (post.getString("tipus").equals("image")){
                    new_dataList.add(0, new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("_id")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video")){
                    new_dataList.add(0, new ListElementVideo(post.getString("owner"), post.getString("text"), post.getString("url_video"), post.getString("_id")));
                    multiAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("doubt")){
                    new_dataList.add(0, new ListElementDoubt(post.getString("owner"), post.getString("titol"), post.getString("text"), post.getString("_id")));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void selectPost(String idPost, View v){
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
                if (dadesPostResult.getJSONArray("likes").get(i).equals(dadesUsuari.getString("_id"))){
                    myLike = true;
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        viewSelectedPost(resultSinglePost, myLike, v);

    }

    public void viewSelectedPost(String infoPost, boolean myLike, View v) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "perfil");
        bundle.putSerializable("isLiked", myLike);
        Navigation.findNavController(v).navigate(R.id.action_navigation_user_to_viewPostFragment, bundle);


        //Carregar el nou fragment amb les seves dades
        /*FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "perfil");
        bundle.putSerializable("isLiked", myLike);
        transaction.replace(R.id.viewFragment, viewPostFragment);
        viewPostFragment.setArguments(bundle);
        transaction.setReorderingAllowed(true);
        transaction.addToBackStack(null);
        transaction.commit();*/
    }
}