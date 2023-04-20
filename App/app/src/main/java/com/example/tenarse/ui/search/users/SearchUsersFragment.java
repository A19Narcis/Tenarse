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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchUsersFragment extends Fragment {

    private FragmentSearchUsersBinding binding;

    /*Adapter and List*/
    List<Object> dataSearchList;
    AdapterSearchUers myAdpater;

    private final String URL = "http://10.0.2.2:3000/searchUsers";

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

        JSONObject body = new JSONObject();

        try {
            body.put("username", query);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        MyAsyncTaskSearchUsers searchUsers = new MyAsyncTaskSearchUsers(URL, body);
        searchUsers.execute();

        String resultSearchUser = null;
        try {
            resultSearchUser = searchUsers.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        try {
            JSONArray resultSearchJSONArray = new JSONArray(resultSearchUser);
            for (int i = 0; i < resultSearchJSONArray.length(); i++) {
                try {
                    JSONObject user = resultSearchJSONArray.getJSONObject(i);
                    dataSearchList.add(new ListElementUser(user.getString("url_img"), user.getString("username"), user.getString("nombre") + " " + user.getString("apellidos")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        binding.rvSearchUsers.setHasFixedSize(true);
        binding.rvSearchUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchUsers.setAdapter(myAdpater);
    }
}