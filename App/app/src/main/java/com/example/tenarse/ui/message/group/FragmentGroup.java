package com.example.tenarse.ui.message.group;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.message.adapters.ChatAdapter;
import com.example.tenarse.ui.message.chat.chatObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FragmentGroup extends Fragment {

    RecyclerView recyclerView;
    ArrayList<chatObject> arrayRecycler = new ArrayList<>();
    ChatAdapter chatAdapter;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        chatAdapter = new ChatAdapter(arrayRecycler, getContext());
        getMyChats();
        recyclerView = rootView.findViewById(R.id.rv_groups);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        return rootView;
    }


    private void getMyChats() {
        String url_chats = "http://212.227.40.235:3000/getAllMyChats";
        JSONObject body = new JSONObject();
        try {
            body.put("_id", dadesUsuari.get("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTask addQuestionTask = new MyAsyncTask(url_chats, body);
        addQuestionTask.execute();
        String resultSuggestedUsers = null;
        try {
            resultSuggestedUsers = addQuestionTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            JSONArray arrayChats = new JSONArray(resultSuggestedUsers);
            for (int i = 0; i < arrayChats.length(); i++) {
                if (arrayChats.getJSONObject(i).getString("tipo").equals("grupo")){
                    JSONObject json = arrayChats.getJSONObject(i);
                    JSONArray participants = json.getJSONArray("participants");
                    String idFotoChat = null;
                    ArrayList <String> realUsername = new ArrayList<>();
                    JSONArray arrayParticipants = new JSONArray();
                    for (int j = 0; j < participants.length(); j++) {
                        idFotoChat = participants.get(j).toString();
                        realUsername.add(getUsernameandImageFromID(idFotoChat));
                        if (!dadesUsuari.getString("_id").equals(participants.get(j))) {
                            JSONObject newUser = new JSONObject();
                            newUser.put("id", idFotoChat);

                            newUser.put("username", new JSONObject(realUsername.get(j)).getString("username"));
                            arrayParticipants.put(newUser);
                        }
                    }
                    String groupName = "";
                    for (int j = 0; j < realUsername.size(); j++) {
                        if(j == realUsername.size() - 1){
                            groupName += "@" + new JSONObject(realUsername.get(j)).getString("username");
                        }else if(j == 0){
                            groupName += new JSONObject(realUsername.get(j)).getString("username") + ", ";
                        }else {
                            groupName += "@" + new JSONObject(realUsername.get(j)).getString("username") + ", ";
                        }
                    }
                    if(groupName.length() > 25) {
                        groupName = groupName.substring(0, 25) + "...";
                    }
                    String lastMsg = "";
                    if (json.getJSONArray("messages").length() > 0) {
                        lastMsg = json.getJSONArray("messages").getJSONObject(json.getJSONArray("messages").length() - 1).getString("txt_msg");
                    }

                    arrayRecycler.add(new chatObject(json.getString("_id"), groupName, lastMsg, "http://212.227.40.235:3000/uploads/user_img/default_user_img.png", arrayParticipants));
                    chatAdapter.notifyItemInserted(arrayRecycler.size() - 1);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsernameandImageFromID(String idUser) {
        String url_selectUser = "http://212.227.40.235:3000/getUsernameAndImageFromID";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_user", idUser);
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
}