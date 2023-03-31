package com.example.tenarse.ui.newpost;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.tenarse.MainActivity;
import com.example.tenarse.databinding.FragmentAddpostBinding;
import com.google.android.material.navigation.NavigationBarView;

public class NewpostFragment extends Fragment {

    private FragmentAddpostBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        NewpostViewModel newpostViewModel =
                new ViewModelProvider(this).get(NewpostViewModel.class);

        binding = FragmentAddpostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.cancelarPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}