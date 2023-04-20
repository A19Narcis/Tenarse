package com.example.tenarse.ui.search.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentSearchUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchUsersFragment extends Fragment {

    private FragmentSearchUsersBinding binding;

    /*Adapter and List*/
    List<Object> dataSearchList;
    AdapterSearchUers myAdpater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchUsersBinding.inflate(inflater, container, false);

        dataSearchList = new ArrayList<>();
        myAdpater = new AdapterSearchUers(dataSearchList, getContext());

        return binding.getRoot();
    }

    public void buscarQuery(String query) {
        /* AÑADIR LOS USUARIOS A LA RECYCLER VIEW */
        dataSearchList = new ArrayList<>();
        myAdpater = new AdapterSearchUers(dataSearchList, getContext());




        dataSearchList.add(new ListElementUser("http://localhost:3000/uploads/user_img/default_user_img.png", "A19Narcis", "Narcis Gomez Carretero"));


        binding.rvSearchUsers.setHasFixedSize(true);
        binding.rvSearchUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchUsers.setAdapter(myAdpater);
    }
}