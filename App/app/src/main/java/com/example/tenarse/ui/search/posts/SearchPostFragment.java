package com.example.tenarse.ui.search.posts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentSearchPostBinding;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.elements.ListElementVideo;

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
            //binding.numPosts.setText("#" + query + "(" + resultSearchPostsArray.length() + ") publicaciones");

            for (int i = 0; i < resultSearchPostsArray.length(); i++) {
                JSONObject post = resultSearchPostsArray.getJSONObject(i);
                if (post.getString("tipus").equals("image")){
                    dataListSearch.add(0, new ListElementImg(post.getString("owner"), post.getString("text"), post.getString("url_img"), post.getString("_id")));
                    myAdapter.notifyItemInserted(0);
                } else if (post.getString("tipus").equals("video")){
                    dataListSearch.add(0, new ListElementVideo(post.getString("owner"), post.getString("text"), post.getString("url_video"), post.getString("_id")));
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
}