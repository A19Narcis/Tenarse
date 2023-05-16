package com.example.tenarse.ui.search.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.databinding.FragmentSearchUsersBinding;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.search.SearchFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchUsersFragment extends Fragment{

    private FragmentSearchUsersBinding binding;

    /*Adapter and List*/
    List<Object> dataSearchList;
    AdapterSearchUsers myAdpater;
    private final String URL = "http://10.0.2.2:3000/searchUsers";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchUsersBinding.inflate(inflater, container, false);

        dataSearchList = new ArrayList<>();
        myAdpater = new AdapterSearchUsers(dataSearchList, getContext(), SearchUsersFragment.this);

        return binding.getRoot();
    }

    public void buscarQuery(String query) {
        /* AÑADIR LOS USUARIOS A LA RECYCLER VIEW */
        dataSearchList = new ArrayList<>();
        myAdpater = new AdapterSearchUsers(dataSearchList, getContext(), SearchUsersFragment.this);

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
            if (resultSearchJSONArray.length() == 0){
                binding.rvSearchUsers.setVisibility(View.GONE);
                binding.noUsersText.setVisibility(View.VISIBLE);
            } else {
                binding.rvSearchUsers.setVisibility(View.VISIBLE);
                binding.noUsersText.setVisibility(View.GONE);
                for (int i = 0; i < resultSearchJSONArray.length(); i++) {
                    try {
                        JSONObject user = resultSearchJSONArray.getJSONObject(i);
                        dataSearchList.add(new ListElementUser(user.getString("_id"), user.getString("url_img"), user.getString("username"), user.getString("nombre") + " " + user.getString("apellidos"), user.getJSONArray("followers").length(), user.getJSONArray("followings").length(), user.getJSONArray("publicacions")));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        binding.rvSearchUsers.setHasFixedSize(true);
        binding.rvSearchUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchUsers.setAdapter(myAdpater);
    }

    public void selectUser(String id_user, View v) {
        //Recoger todos los datos del usuario que tiene ese `username` y luego cambiar de fragment para ver su perfil
        String url_selectUser = "http://10.0.2.2:3000/getUserById";
        JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("id_user", id_user);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
        selectedUser.execute();
        String resultSearch = null;
        try {
            resultSearch = selectedUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject dadesUser = new JSONObject(resultSearch);
            viewSelectedUser(dadesUser, v);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void viewSelectedUser(JSONObject dadesUser, View view) {
        SearchFragment searchFragment = (SearchFragment) getParentFragment();
        searchFragment.seeProfileUser(dadesUser, view);
    }
}