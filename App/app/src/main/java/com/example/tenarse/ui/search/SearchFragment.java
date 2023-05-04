package com.example.tenarse.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentSearchBinding;
import com.example.tenarse.ui.profile.ProfileFragment;
import com.example.tenarse.ui.search.posts.SearchPostFragment;
import com.example.tenarse.ui.search.questions.SearchQuestionsFragment;
import com.example.tenarse.ui.search.users.ListElementUser;
import com.example.tenarse.ui.search.users.SearchUsersFragment;

import org.json.JSONObject;

import java.io.Serializable;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    private final int USER_SELECTED = 1;
    private final int POST_SELECTED = 2;
    private final int QUESTION_SELECTED = 3;

    private int selected_image = USER_SELECTED;

    private SearchPostFragment searchPostFragment;
    private SearchQuestionsFragment searchQuestionsFragment;
    private SearchUsersFragment searchUsersFragmentClicked;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Se ve el fragment de usuarios cuando accede al fragment
        binding.buscador.setQueryHint("Busca usuarios");
        SearchUsersFragment searchUsersFragment = new SearchUsersFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container_search, searchUsersFragment);
        transaction.commit();


        //Cambio de fragments cuando clica las imagenes
        binding.imgSearchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.buscador.setQueryHint("Busca usuarios");
                selected_image = USER_SELECTED;
                binding.imgSearchUser.setImageResource(R.drawable.selected_user);
                binding.imgSearchPost.setImageResource(R.drawable.unsel_videos);
                binding.imgSearchDoubt.setImageResource(R.drawable.unsel_questions);
                searchUsersFragmentClicked = new SearchUsersFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container_search, searchUsersFragmentClicked);
                transaction.commit();
            }
        });

        binding.imgSearchPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.buscador.setQueryHint("Java, HTML, ChatGPT,...");
                selected_image = POST_SELECTED;
                binding.imgSearchPost.setImageResource(R.drawable.selected_videos);
                binding.imgSearchUser.setImageResource(R.drawable.unsel_user);
                binding.imgSearchDoubt.setImageResource(R.drawable.unsel_questions);
                searchPostFragment = new SearchPostFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container_search, searchPostFragment);
                transaction.commit();
            }
        });

        binding.imgSearchDoubt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.buscador.setQueryHint("¿Alguna duda?");
                selected_image = QUESTION_SELECTED;
                binding.imgSearchDoubt.setImageResource(R.drawable.selected_questions);
                binding.imgSearchPost.setImageResource(R.drawable.unsel_videos);
                binding.imgSearchUser.setImageResource(R.drawable.unsel_user);
                searchQuestionsFragment = new SearchQuestionsFragment();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container_search, searchQuestionsFragment);
                transaction.commit();
            }
        });



        binding.logoSearchToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.scrollView.smoothScrollTo(0,0);
            }
        });

        binding.buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")){
                    switch (selected_image){
                        case USER_SELECTED:
                            if (searchUsersFragmentClicked == null){
                                searchUsersFragment.buscarQuery(query);
                            } else {
                                searchUsersFragmentClicked.buscarQuery(query);
                            }
                            break;
                        case POST_SELECTED:
                            searchPostFragment.buscarQuery("#"+query);
                            break;
                        case QUESTION_SELECTED:
                            searchQuestionsFragment.buscarQuery(query);
                            break;
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals("")){
                    System.out.println("");
                }
                return true;
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void seeProfileUser(JSONObject dadesUser, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("userInfo", dadesUser.toString());
        Navigation.findNavController(view).navigate(R.id.action_navigation_search_to_profileFragment, bundle);
    }

    public void seeSelectedPost(String resultSinglePost, boolean myLike, View view, String username) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", resultSinglePost);
        bundle.putSerializable("origin", "search");
        bundle.putSerializable("isLiked", myLike);
        bundle.putSerializable("usernamePost", username);
        Navigation.findNavController(view).navigate(R.id.action_navigation_search_to_viewPostFragment, bundle);
    }
}