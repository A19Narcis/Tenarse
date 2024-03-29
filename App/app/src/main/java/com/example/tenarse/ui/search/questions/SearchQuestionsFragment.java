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
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.search.SearchFragment;

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

    private final String URL = "http://212.227.40.235:3000/searchDoubt";

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

        MyAsyncTask searchPosts = new MyAsyncTask(URL, body);
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
                    //SACAR USERNAME & IMAGE URL
                    String realUsername = getUsernameandImageFromID(post);
                    JSONObject username_image = new JSONObject(realUsername);
                    ListElementDoubt listElementDoubt = new ListElementDoubt(post.getString("_id"), username_image.getString("username"), post.getString("titol"), post.getString("text"),  username_image.getString("url_img"), post.getJSONArray("likes"));
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

    private String getUsernameandImageFromID(JSONObject post) {
        String url_selectUser = "http://212.227.40.235:3000/getUsernameAndImageFromID";
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

    public void addLike(String id) {
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();
        isLiked = true;
        String url = "http://212.227.40.235:3000/newLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", id);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTask likesTask = new MyAsyncTask(url, body);
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
        String url = "http://212.227.40.235:3000/removeLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", id);
            body.put("id_user", dadesUser.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTask likesTask = new MyAsyncTask(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void selectPost(String id_post, View v, String username, String url_img) {
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();
        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://212.227.40.235:3000/getSelectedPost/" + id_post;
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

        viewSelectedPost(resultSinglePost, myLike, v, username, url_img);
    }

    private void viewSelectedPost(String resultSinglePost, boolean myLike, View v, String username, String url_img){
        SearchFragment searchFragment = (SearchFragment) getParentFragment();
        searchFragment.seeSelectedPost(resultSinglePost, myLike, v, username, url_img);
    }

    public void selectUser(String username, View v) {
        String url_selectUser = "http://212.227.40.235:3000/getSelectedUser";
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
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

    private void viewSelectedUser(JSONObject dadesUser, View v) {
        SearchFragment searchFragment = (SearchFragment) getParentFragment();
        searchFragment.seeProfileUser(dadesUser, v);
    }
}