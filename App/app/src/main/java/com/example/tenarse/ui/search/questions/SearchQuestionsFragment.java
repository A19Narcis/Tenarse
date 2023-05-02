package com.example.tenarse.ui.search.questions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.databinding.FragmentSearchQuestionsBinding;
import com.example.tenarse.ui.search.posts.AdapterSearchPost;
import com.example.tenarse.ui.search.posts.ListElementImg;
import com.example.tenarse.ui.search.posts.ListElementVideo;
import com.example.tenarse.ui.search.posts.MyAsyncTaskGetPosts;
import com.example.tenarse.ui.search.posts.SearchPostFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchQuestionsFragment extends Fragment {

    private FragmentSearchQuestionsBinding binding;

    List<Object> dataListSearch;
    AdapterSearchQuestions myAdapter;

    private final String URL = "http://10.0.2.2:3000/searchDoubt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataListSearch = new ArrayList<>();
        myAdapter = new AdapterSearchQuestions(dataListSearch, getContext(), SearchQuestionsFragment.this);

        binding = FragmentSearchQuestionsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();




        return rootView;
    }

    public void buscarQuery(String query) {
        dataListSearch = new ArrayList<>();
        myAdapter = new AdapterSearchQuestions(dataListSearch, getContext(), SearchQuestionsFragment.this);

        JSONObject body = new JSONObject();

        try {
            body.put("query", query);
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
            if (resultSearchPostsArray.length() == 0){
                binding.rvSearchQuestions.setVisibility(View.GONE);
                binding.noDoubtText.setText("No hay dudas de " + query);
                binding.noDoubtText.setVisibility(View.VISIBLE);
            } else {
                binding.rvSearchQuestions.setVisibility(View.VISIBLE);
                binding.noDoubtText.setVisibility(View.GONE);
                for (int i = 0; i < resultSearchPostsArray.length(); i++) {
                    JSONObject post = resultSearchPostsArray.getJSONObject(i);
                    dataListSearch.add(0, new ListElementDoubt(post.getString("_id"), post.getString("owner"), post.getString("titol"), post.getString("text"),  post.getString("user_img"), post.getJSONArray("likes")));
                    myAdapter.notifyItemInserted(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        binding.rvSearchQuestions.setHasFixedSize(true);
        binding.rvSearchQuestions.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvSearchQuestions.setAdapter(myAdapter);
    }

    public void addLike(String id) {
    }

    public void removeLike(String id) {
    }

    public void selectPost(String id, View v) {
    }

    public void selectUser(String toString, View v) {
    }
}