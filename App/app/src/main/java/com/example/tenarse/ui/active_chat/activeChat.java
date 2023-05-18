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
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentActiveChatBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.ui.active_chat.adapters.ActiveChatMultiAdapter;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;

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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject message = null;
                            try {
                                message = new JSONObject(args[0].toString());
                                JSONObject realUsernameObject = getOwnerPost(message.getString("emisor"));
                                String realUsernameStr = realUsernameObject.getString("username");
                                if(!message.getString("emisor").equals(dadesUsuari.getString("_id")) && !message.getString("message").equals("")){
                                    arrayRecycler.add(new MessageObject(message.getString("emisor"), realUsernameStr, message.getString("message")));
                                } else if (!message.getString("emisor").equals(dadesUsuari.getString("_id")) && message.getString("message").equals("")) {
                                    JSONObject datos_post = getPost(message.getString("post_id"));
                                    if (datos_post != null) {
                                        JSONObject datos_owner = getOwnerPost(datos_post.getString("owner"));
                                        if (datos_post.getString("tipus").equals("doubt")){
                                            arrayRecycler.add(new PostObject(message.getString("emisor"), realUsernameStr, message.getString("post_id"), datos_post.getString("url_img"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_post.getString("titol")));
                                        } else if (datos_post.getString("tipus").equals("image")){
                                            arrayRecycler.add(new PostObject(message.getString("emisor"), realUsernameStr, message.getString("post_id"), datos_post.getString("url_img"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_owner.getString("username")));
                                        } else if (datos_post.getString("tipus").equals("video")){
                                            arrayRecycler.add(new PostObject(message.getString("emisor"), realUsernameStr, message.getString("post_id"), datos_post.getString("url_video"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_owner.getString("username")));
                                        }
                                    } else {
                                        arrayRecycler.add(new MessageObject(message.getString("emisor"), realUsernameStr, "/*Publicación eliminada*/"));
                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            chatAdapter.notifyItemInserted(arrayRecycler.size()-1);
                            recyclerView.scrollToPosition(arrayRecycler.size() - 1);
                        }
                    });
                }

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
            try {
                arryParticipants = new JSONArray(args.getString("participants"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        chatAdapter = new ActiveChatMultiAdapter(arrayRecycler, getContext(),activeChat.this);
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
                    body.put("message", binding.msgTextView.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("sendMessage", body);
                sendMessage();
                recyclerView.scrollToPosition(arrayRecycler.size() - 1);
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
            body.put("post_id", "");
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
                        JSONObject datos_post = getPost(object.getString("post_id"));
                        if (datos_post != null) {
                            JSONObject datos_owner = getOwnerPost(datos_post.getString("owner"));
                            if(datos_post.getString("tipus").equals("image")) {
                                arrayRecycler.add(new MyPostObject(object.getString("emisor"), userRealName, object.getString("post_id"), datos_post.getString("url_img"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_owner.getString("username")));
                            }else if(datos_post.getString("tipus").equals("video")){
                                arrayRecycler.add(new MyPostObject(object.getString("emisor"), userRealName, object.getString("post_id"), datos_post.getString("url_video"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_owner.getString("username")));
                            }else{
                                arrayRecycler.add(new MyPostObject(object.getString("emisor"), userRealName, object.getString("post_id"), datos_post.getString("url_video"), datos_owner.getString("url_img"), datos_post.getString("titol"), datos_owner.getString("username")));
                            }
                        }else{
                            arrayRecycler.add(new MyMessageObject(object.getString("emisor"), userRealName, "/*Publicación eliminada*/"));
                        }
                    }else if(!object.getString("emisor").equals(dadesUsuari.getString("_id")) && object.getString("txt_msg").equals("")){
                        JSONObject datos_post = getPost(object.getString("post_id"));
                        System.out.println(datos_post);
                        if (datos_post != null) {
                            JSONObject datos_owner = getOwnerPost(datos_post.getString("owner"));
                            if(datos_post.getString("tipus").equals("image")) {
                                arrayRecycler.add(new PostObject(object.getString("emisor"), userRealName, object.getString("post_id"), datos_post.getString("url_img"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_owner.getString("username")));
                            }else if(datos_post.getString("tipus").equals("video")){
                                arrayRecycler.add(new PostObject(object.getString("emisor"), userRealName, object.getString("post_id"), datos_post.getString("url_video"), datos_owner.getString("url_img"), datos_post.getString("text"), datos_owner.getString("username")));
                            }else{
                                arrayRecycler.add(new PostObject(object.getString("emisor"), userRealName, object.getString("post_id"), datos_post.getString("url_video"), datos_owner.getString("url_img"), datos_post.getString("titol"), datos_owner.getString("username")));
                            }
                        }else{
                            arrayRecycler.add(new MessageObject(object.getString("emisor"), userRealName, "/*Publicación eliminada*/"));
                        }
                    }
                }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        chatAdapter.notifyItemInserted(arrayRecycler.size()-1);
        binding.msgTextView.setText("");
    }

    private JSONObject getPost(String post_id) {
        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://212.227.40.235:3000/getSelectedPost/" + post_id;
        MyAsyncTaskGetSinglePost getSinglePost = new MyAsyncTaskGetSinglePost(url_selectPost);
        getSinglePost.execute();
        String resultSinglePost = null;
        JSONObject datos_post = null;
        try {
            resultSinglePost = getSinglePost.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            datos_post = new JSONObject(resultSinglePost);
        } catch (JSONException e) {
            datos_post = null;
        }
        return datos_post;
    }

    private JSONObject getOwnerPost(String idUser) {
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
        JSONObject datos_owner = null;
        try {
            resultSearch = selectedUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            datos_owner = new JSONObject(resultSearch);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return datos_owner;
    }

    public void selectPost(String idPost, View view, String username, String url_img){
        //Recoger todos los datos de un post y verlos en un fragment nuevo
        String url_selectPost = "http://212.227.40.235:3000/getSelectedPost/" + idPost;
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
                if (dadesPostResult.getJSONArray("likes").get(i).equals(GlobalDadesUser.getInstance().getDadesUser().getString("_id"))){
                    myLike = true;
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        viewSelectedPost(resultSinglePost, myLike, view, username, url_img);
    }

    public void viewSelectedPost(String infoPost, boolean myLike, View view, String username, String url_img) {
        //Carregar el nou fragment amb les seves dades
        Bundle bundle = new Bundle();
        bundle.putSerializable("infoPost", infoPost);
        bundle.putSerializable("origin", "home");
        bundle.putSerializable("isLiked", myLike);
        bundle.putSerializable("usernamePost", username);
        bundle.putSerializable("url_img", url_img);
        Navigation.findNavController(view).navigate(R.id.action_activeChat_to_viewPostFragment, bundle);
    }
}
