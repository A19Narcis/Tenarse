package com.example.tenarse.ui.user;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentUserBinding;
import com.example.tenarse.ui.user.elements.ListElementDoubt;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.adapters.MultiAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    List<Object> dataList;
    private FragmentUserBinding binding;

    RecyclerView recyclerView;

    MultiAdapter MultiAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserViewModel userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        dataList = new ArrayList<>();
        MultiAdapter = new MultiAdapter(dataList);
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_user_to_navigation_settings);
            }
        });

        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));
        dataList.add(new ListElementImg("_A19Narcis_", ""));
        dataList.add(new ListElementImg("_A19Narcis_", "Me encanta el juego que estoy haciendo"));


        recyclerView = binding.recyclerViewFeed;

        //Calculamos cuanto va a medir la recyclerview según la cantidad de publicaciones que existen
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int pixels = (int) (129 * (metrics.ydpi / 160));
        float floatPixels = (129 * (metrics.ydpi / 160));
        System.out.println(metrics.ydpi);

        binding.recyclerViewFeed.setMinimumHeight((int) (((dataList.size() / 3) + 1) * (350 + 12)));
        System.out.println((floatPixels));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(MultiAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}