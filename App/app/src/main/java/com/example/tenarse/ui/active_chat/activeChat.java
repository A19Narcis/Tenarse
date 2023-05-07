package com.example.tenarse.ui.active_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.message.adapters.ChatAdapter;
import com.example.tenarse.ui.message.chat.chatObject;

import org.json.JSONObject;

import java.util.ArrayList;

public class activeChat extends Fragment {
    RecyclerView recyclerView;
    ArrayList<chatObject> arrayRecycler = new ArrayList<>();
    ChatAdapter chatAdapter;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_active_chat, container, false);

        Bundle args = getArguments();
        /*if (args != null){
            infoPost = args.getString("infoPost");
            fragmentAnterior = args.getString("fragment");
            originFragment = args.getString("origin");
            isLiked = args.getBoolean("isLiked");
            usernamePost = args.getString("usernamePost");
            urlImg = args.getString("url_img");
        }                                                   HAY QUE RECOGER LOS DATOS DE CHATADAPTEEEEEEEEER*/

        chatAdapter = new ChatAdapter(arrayRecycler, getContext());

        recyclerView = rootView.findViewById(R.id.rv_chat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        return rootView;
    }


}