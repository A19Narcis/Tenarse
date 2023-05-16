package com.example.tenarse.ui.active_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.MainActivity;
import com.example.tenarse.databinding.FragmentActiveChatBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.ui.active_chat.adapters.ActiveChatMultiAdapter;
import com.example.tenarse.globals.MyAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class activeChat extends Fragment {

    private FragmentActiveChatBinding binding;
    RecyclerView recyclerView;
    ArrayList<Object> arrayRecycler = new ArrayList<>();
    ActiveChatMultiAdapter chatAdapter;

    GlobalDadesUser globalDadesUser;
    JSONObject dadesUsuari;

    String chat_id;

    String username;

    String profile_img;

    String receptor_id;

    ApiService apiService;

    JSONArray arryParticipants;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActiveChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        MainActivity mainActivity = (MainActivity) getActivity();
        Socket mSocket = mainActivity.getmSocket();
        mSocket.on("listenChats", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject message = null;
                try {
                    message = new JSONObject(args[0].toString());
                    arrayRecycler.add(new MessageObject(message.getString("emisor"), message.getString("username"), message.getString("message")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                chatAdapter.notifyItemInserted(arrayRecycler.size()-1);
            }
        });

        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });


        Bundle args = getArguments();
        if (args != null){
            receptor_id = args.getString("receptor_id");
            chat_id = args.getString("chat_id");
            username = args.getString("username");
            profile_img = args.getString("profile_img");
            try {
                arryParticipants = new JSONArray(args.getString("participants"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        /*arrayRecycler.add(new MessageObject("1","sergi", "Hola que tal?"));
        arrayRecycler.add(new MessageObject("2","teo", "Toi bien"));
        arrayRecycler.add(new PostObject("2","http://localhost:3000/uploads\\user_img\\6459f1fb6e52fac56af41ad7.png", "http://localhost:3000/uploads\\user_img\\6459f1fb6e52fac56af41ad7.png", "@narcis", ""));
*/
        chatAdapter = new ActiveChatMultiAdapter(arrayRecycler, getContext(), new activeChat());
        recyclerView = binding.rvChat;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        getOldMessages();

        recyclerView.scrollToPosition(arrayRecycler.size() - 1);

        binding.sendBtn.setOnClickListener(view -> {
            if(!binding.msgTextView.getText().toString().equals("")) {
                JSONObject body = new JSONObject();

                try {
                    body.put("chat_id", chat_id);
                    body.put("emisor", dadesUsuari.getString("_id"));
                    body.put("username", dadesUsuari.getString("username"));
                    body.put("message", binding.msgTextView.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("sendMessage", body);
                sendMessage();
            }
        });
        return root;
    }

    public void sendMessage(){
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUsuari = globalDadesUser.getDadesUser();
        JSONObject body = new JSONObject();

        try {
            body.put("chat_id", chat_id);
            body.put("emisor", dadesUsuari.getString("_id"));
            body.put("message", binding.msgTextView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url_updateDades = "http://212.227.40.235:3000/newMessage";
        MyAsyncTask updateUser = new MyAsyncTask(url_updateDades, body);
        updateUser.execute();
        String resultUpdate = null;
        try {
            resultUpdate = updateUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            arrayRecycler.add(new MyMessageObject(dadesUsuari.getString("_id"), dadesUsuari.getString("username"), binding.msgTextView.getText().toString()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        chatAdapter.notifyItemInserted(arrayRecycler.size()-1);
        binding.msgTextView.setText("");
    }

    public void getOldMessages(){
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUsuari = globalDadesUser.getDadesUser();
        JSONObject body = new JSONObject();

        try {
            body.put("chat_id", chat_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url_updateDades = "http://212.227.40.235:3000/getMessages";
        MyAsyncTask updateUser = new MyAsyncTask(url_updateDades, body);
        updateUser.execute();
        String resultUpdate = null;
        try {
            resultUpdate = updateUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            JSONObject objectChat = new JSONObject(resultUpdate);
                JSONArray arrayMessages = objectChat.getJSONObject("chat").getJSONArray("messages");
                for (int j = 0; j < arrayMessages.length(); j++) {//Coje la array de mensajes
                    JSONObject object = (JSONObject) arrayMessages.get(j);
                    String userRealName = dadesUsuari.getString("username");
                    boolean userFound = false;
                    for (int k = 0; k < arryParticipants.length() && !userFound; k++) {
                        if(object.getString("emisor").equals(arryParticipants.getJSONObject(k).getString("id"))){
                            userRealName = arryParticipants.getJSONObject(k).getString("username");
                            userFound = true;
                        }
                    }
                    if(object.getString("emisor").equals(dadesUsuari.getString("_id")) && !object.getString("txt_msg").equals("")){
                        arrayRecycler.add(new MyMessageObject(object.getString("emisor"), userRealName, object.getString("txt_msg")));
                    }else if(!object.getString("emisor").equals(dadesUsuari.getString("_id")) && !object.getString("txt_msg").equals("")){
                        arrayRecycler.add(new MessageObject(object.getString("emisor"), userRealName, object.getString("txt_msg")));
                    }else if(object.getString("emisor").equals(dadesUsuari.getString("_id")) && object.getString("txt_msg").equals("")){
                        //arrayRecycler.add(new MyPostObject(object.getString("post_id"), userRealName, object.getString("txt_msg")));
                    }
                }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        chatAdapter.notifyItemInserted(arrayRecycler.size()-1);
        binding.msgTextView.setText("");
    }


}
