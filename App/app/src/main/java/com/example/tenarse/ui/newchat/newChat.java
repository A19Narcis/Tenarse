package com.example.tenarse.ui.newchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentChatBinding;
import com.example.tenarse.databinding.FragmentCreateNewChatBinding;
import com.example.tenarse.databinding.FragmentNotificacionesBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.message.adapters.ChatAdapter;
import com.example.tenarse.ui.message.chat.chatObject;
import com.example.tenarse.ui.newchat.adapters.SuggestedUsersAdapter;
import com.example.tenarse.ui.newchat.asynctask.MyAsyncTaskSuggestedUsers;
import com.example.tenarse.ui.newpost.NewpostFragment;
import com.example.tenarse.ui.newpost.httpUploads.MyAsyncTaskQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class newChat extends Fragment {
    private FragmentCreateNewChatBinding binding;
    RecyclerView recyclerView;
    ArrayList<SuggestedUsersObject> arrayRecycler = new ArrayList<>();
    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateNewChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        getSuggestedUsers();

        arrayRecycler.add(new SuggestedUsersObject("", "@SergiMS03"));
        SuggestedUsersAdapter suggestedUsersAdapter = new SuggestedUsersAdapter(arrayRecycler, getContext());
        recyclerView = binding.recyclerViewNewChat;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(suggestedUsersAdapter);


        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });



        return root;
    }

    private void getSuggestedUsers() {
        String url_register = "http://10.0.2.2:3000/getSuggestedUsersChat";
        JSONObject body = new JSONObject();
        try {
            body.put("_id", dadesUsuari.get("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskSuggestedUsers addQuestionTask = new MyAsyncTaskSuggestedUsers(url_register, body);
        addQuestionTask.execute();
        String resultAddQuestion = null;
        try {
            resultAddQuestion = addQuestionTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }



    }
}
