package com.example.tenarse.ui.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentUserBinding;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.adapters.ListElementAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;

    RecyclerView recyclerView;

    List<ListElementImg> elementsImg;

    ListElementAdapter listElementAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UserViewModel userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        elementsImg = new ArrayList<>();
        listElementAdapter = new ListElementAdapter(elementsImg, getContext());
        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_user_to_navigation_settings);
            }
        });

        recyclerView = binding.recyclerViewFeed;

        elementsImg.add(new ListElementImg(""));
        elementsImg.add(new ListElementImg(""));
        elementsImg.add(new ListElementImg(""));
        elementsImg.add(new ListElementImg(""));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(listElementAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}