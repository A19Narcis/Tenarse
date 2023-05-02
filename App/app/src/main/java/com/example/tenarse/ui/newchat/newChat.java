package com.example.tenarse.ui.newchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tenarse.databinding.FragmentChatBinding;
import com.example.tenarse.databinding.FragmentCreateNewChatBinding;
import com.example.tenarse.databinding.FragmentNotificacionesBinding;

public class newChat extends Fragment {

    private FragmentCreateNewChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateNewChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });



        return root;
    }
}
