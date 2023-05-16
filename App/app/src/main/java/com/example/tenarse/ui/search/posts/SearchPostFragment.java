package com.example.tenarse.ui.search.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.databinding.FragmentSearchPostBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.search.SearchFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class SearchPostFragment extends Fragment {

    private FragmentSearchPostBinding binding;

    List<Object> dataListSearch;
    AdapterSearchPost myAdapter;

    private final String URL = "http://10.0.2.2:3000/searchPost";

    private GlobalDadesUser globalDadesUser;
    private JSONObject dadesUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataListSearch = new ArrayList<>();
        myAdapter = new AdapterSearchPost(dataListSearch, getContext(), SearchPostFragment.this);

        binding = FragmentSearchPostBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();




        return rootView;
    }

    public void buscarQuery(String query) {
        dataListSearch = new ArrayList<>();
        myAdapter = new AdapterSearchPost(dataListSearch, getContext(), SearchPostFragment.this);

        JSONObject body = new JSONObject();

        try {
            body.put("hashtag", query);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskGetPosts searchPosts = new MyAsyncTaskGetPosts(URL, body);
        searchPosts.execute();
        String resultSearchPost = null;
        try {
            resultSearchPost = searchPosts.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Pintarlos en la RecyclerView
        try {
            JSONArray resultSearchPostsArray = new JSONArray(resultSearchPost);
            binding.numPosts.setText(query.toLowerCase() + " (" + resultSearchPostsArray.length() + " publicaciones)");

            for (int i = 0; i < resultSearchPostsArray.length(); i++) {
                JSONObject post = resultSearchPostsArray.getJSONObject(i);
                //SACAR USERNAME
                String realUsername = getUsernameFromID(post);
                JSONObject jsonRealDades = new JSONObject(realUsername);
                if (post.getString("tipus").equals("image")){
                    dataListSearch.add(0, new ListElementImg(jsonRealDades.getString("username"), post.getString("text"), post.getString("url_img"), post.getString("_id"), jsonRealDades.getString("url_img")));
                    myAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video")){
                    dataListSearch.add(0, new ListElementVideo(jsonRealDades.getString("username"), post.getString("text"), post.getString("url_video"), post.getString("_id"), jsonRealDades.getString("url_img")));
                    myAdapter.notifyItemInserted(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.rvSearchPosts.setHasFixedSize(true);
        binding.rvSearchPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.rvSearchPosts.setAdapter(myAdapter);
    }

    private String getUsernameFromID(JSONObject post) {
        String url_selectUser = "http://10.0.2.2:3000/getUsernameAndImageFromID";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_user", post.getString("owner"));
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
        return resultSearch;
    }


    public void selectPost(String id_post, View view, String username, String url_img) {
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();

        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://10.0.2.2:3000/getSelectedPost/" + id_post;
        MyAsyncTaskGetSinglePost getSinglePost = new MyAsyncTaskGetSinglePost(url_selectPost);
        getSinglePost.execute();
        String resultSinglePost = null;
        try {
            resultSinglePost = getSinglePost.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Ver si el post que hemos seleccionado tiene mi `Like`
        boolean myLike = false;
        try {
            JSONObject dadesPostResult = new JSONObject(resultSinglePost);
            for (int i = 0; i < dadesPostResult.getJSONArray("likes").length() && !myLike; i++) {
                if (dadesPostResult.getJSONArray("likes").get(i).equals(dadesUser.getString("_id"))){
                    myLike = true;
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        viewSelectedPost(resultSinglePost, myLike, view, username, url_img);
    }

    private void viewSelectedPost(String resultSinglePost, boolean myLike, View view, String username, String url_img) {
        SearchFragment searchFragment = (SearchFragment) getParentFragment();
        searchFragment.seeSelectedPost(resultSinglePost, myLike, view, username, url_img);
    }
}