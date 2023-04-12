package com.example.tenarse.ui.home;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentHomeBinding;
import com.example.tenarse.ui.home.adapters.ListElementImgAdapter;
import com.example.tenarse.ui.home.adapters.ListElementVideoAdapter;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentHomeBinding binding;
    private boolean shouldReloadOnBackPressed = false;
    private ScrollView scrollView;

    List<ListElementImg> elementsImg;
    List<ListElementVideo> elementVideos;

    ListElementImgAdapter listElementImgAdapter;
    ListElementVideoAdapter listElementVideoAdapter;

    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity mainActivity = (MainActivity) getActivity();


        /*RecivlerView Images*/
        elementsImg = new ArrayList<>();
        elementVideos = new ArrayList<>();
        listElementImgAdapter = new ListElementImgAdapter(elementsImg, getContext());
        listElementVideoAdapter = new ListElementVideoAdapter(elementVideos, getContext());
        recyclerView = binding.rvHome;

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

        scrollView = binding.scrollView;


        binding.logoHomeToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,0);
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chechIfNewPost();
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
        elementsImg.add(new ListElementImg("_A19Narcis_"));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listElementImgAdapter);

        /*elementVideos.add(new ListElementVideo("SergiM03"));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(listElementVideoAdapter);*/

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
}