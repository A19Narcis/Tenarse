package com.example.tenarse.ui.search.questions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.databinding.FragmentSearchQuestionsBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskLikes;
import com.example.tenarse.ui.search.SearchFragment;
import com.example.tenarse.ui.search.posts.MyAsyncTaskGetPosts;
import com.example.tenarse.ui.search.users.ListElementUser;

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

    private GlobalDadesUser globalDadesUser;
    private JSONObject dadesUser;

    private boolean isLiked;

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
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();

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
                    isLiked = false;
                    JSONObject post = resultSearchPostsArray.getJSONObject(i);
                    ListElementDoubt listElementDoubt = new ListElementDoubt(post.getString("_id"), post.getString("owner"), post.getString("titol"), post.getString("text"),  post.getString("user_img"), post.getJSONArray("likes"));
                    for (int j = 0; j < listElementDoubt.getLikes().length() && !isLiked; j++) {
                        if (listElementDoubt.getLikes().get(j).toString().equals(dadesUser.getString("_id"))){
                            isLiked = true;
                            listElementDoubt.setLiked(true);
                        }
                    }
                    dataListSearch.add(0, listElementDoubt);
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
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();
        isLiked = true;
        String url = "http://10.0.2.2:3000/newLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", id);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskLikes likesTask = new MyAsyncTaskLikes(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLike(String id) {
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();
        isLiked = false;
        String url = "http://10.0.2.2:3000/removeLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", id);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskLikes likesTask = new MyAsyncTaskLikes(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectPost(String id_post, View v) {
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

        viewSelectedPost(resultSinglePost, myLike, v);
    }

    private void viewSelectedPost(String resultSinglePost, boolean myLike, View v){
        SearchFragment searchFragment = (SearchFragment) getParentFragment();
        searchFragment.seeSelectedPost(resultSinglePost, myLike, v);
    }

    public void selectUser(String username, View v) {
        String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyAsyncTaskGetUser selectedUser = new MyAsyncTaskGetUser(url_selectUser, jsonBody);
        selectedUser.execute();
        String resultSearch = null;
        try {
            resultSearch = selectedUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject dadesLogin = new JSONObject(resultSearch);
            ListElementUser userSelected = new ListElementUser(dadesLogin.getString("_id"), dadesLogin.getString("url_img"), dadesLogin.getString("username"), dadesLogin.getString("nombre") + " " + dadesLogin.getString("apellidos"), dadesLogin.getJSONArray("followers").length(), dadesLogin.getJSONArray("followings").length(), dadesLogin.getJSONArray("publicacions"));
            viewSelectedUser(userSelected, v);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void viewSelectedUser(ListElementUser userSelected, View v) {
        SearchFragment searchFragment = (SearchFragment) getParentFragment();
        searchFragment.seeProfileUser(userSelected, v);
    }
}