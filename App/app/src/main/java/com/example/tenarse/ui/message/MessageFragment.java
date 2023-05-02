package com.example.tenarse.ui.message;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentMessageBinding;
import com.example.tenarse.ui.message.chat.FragmentChat;
import com.example.tenarse.ui.message.group.FragmentGroup;

public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MessageViewModel messageViewModel =
                new ViewModelProvider(this).get(MessageViewModel.class);

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FragmentChat fragmentChat = new FragmentChat();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentChat);
        transaction.commit();

        binding.txtChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.txtChat.setTypeface(null, Typeface.BOLD);
                binding.txtGrupos.setTypeface(null, Typeface.NORMAL);
                FragmentChat fragmentChat = new FragmentChat();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentChat);
                transaction.commit();
            }
        });

        binding.txtGrupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.txtChat.setTypeface(null, Typeface.NORMAL);
                binding.txtGrupos.setTypeface(null, Typeface.BOLD);
                FragmentGroup fragmentGrupos = new FragmentGroup();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentGrupos);
                transaction.commit();
            }
        });

        ImageView newChats = binding.newChats;

        newChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_navigation_nots);
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