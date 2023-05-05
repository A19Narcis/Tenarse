package com.example.tenarse.ui.newchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    Button createChat;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateNewChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        createChat = binding.createChat;
        createChat.setOnClickListener(view -> {
            ArrayList<String> participantes = new ArrayList<>();
            try {
                participantes.add(dadesUsuari.getString("_id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < arrayRecycler.size(); i++) {
                if(arrayRecycler.get(i).isSelected()){
                    participantes.add(arrayRecycler.get(i).getId());
                }
            }
            enviarCrear(participantes);
        });

        getSuggestedUsers();

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
        String resultSuggestedUsers = null;
        try {
            resultSuggestedUsers = addQuestionTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            JSONArray arrayUsers = new JSONArray(resultSuggestedUsers);
            for (int i = 0; i < arrayUsers.length(); i++) {
                JSONObject user = arrayUsers.getJSONObject(i);
                arrayRecycler.add(new SuggestedUsersObject(user.getString("url_img"), user.getString("username"), user.getString("_id")));
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void enviarCrear(ArrayList<String> participantes) {
        String url_register = "http://10.0.2.2:3000/createChat";
        JSONObject body = new JSONObject();
        try {
            body.put("users", participantes);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskSuggestedUsers addQuestionTask = new MyAsyncTaskSuggestedUsers(url_register, body);
        addQuestionTask.execute();
        String resultSuggestedUsers = null;
        try {
            resultSuggestedUsers = addQuestionTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
