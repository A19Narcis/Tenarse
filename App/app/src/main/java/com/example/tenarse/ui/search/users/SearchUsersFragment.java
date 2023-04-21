package com.example.tenarse.ui.search.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentSearchUsersBinding;
import com.example.tenarse.ui.profile.ProfileFragment;
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
        myAdpater = new AdapterSearchUsers(dataSearchList, getContext(), new AdapterSearchUsers.OnItemClickListener() {
            @Override
            public void onItemClick(ListElementUser userClick) {
                System.out.println("VAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMOS");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ProfileFragment profileFragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("userInfo", userClick);
                profileFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.fragment_container_search, profileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return binding.getRoot();
    }

    public void buscarQuery(String query) {
        /* AÑADIR LOS USUARIOS A LA RECYCLER VIEW */
        dataSearchList = new ArrayList<>();
        myAdpater = new AdapterSearchUsers(dataSearchList, getContext(), new AdapterSearchUsers.OnItemClickListener() {
            @Override
            public void onItemClick(ListElementUser userClick) {
                SearchFragment searchFragment = (SearchFragment) getParentFragment();
                if (searchFragment != null){
                    searchFragment.seeProfileUser(userClick);
                }
            }
        });

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
                    dataSearchList.add(new ListElementUser(user.getString("url_img"), user.getString("username"), user.getString("nombre") + " " + user.getString("apellidos"), user.getString("followers").length(), user.getString("followings").length(), user.getJSONArray("publicacions")));
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