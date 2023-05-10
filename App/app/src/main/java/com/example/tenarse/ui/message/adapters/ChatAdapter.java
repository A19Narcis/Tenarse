package com.example.tenarse.ui.message.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.message.chat.chatObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<chatObject> dataList;
    private Context context;

    public ChatAdapter(ArrayList<chatObject> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_chat, parent, false);
        return new chatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        chatViewHolder cvh = (chatViewHolder) holder;
        cvh.textoUsuario.setText("@" + dataList.get(position).getUserName());
        cvh.lastMsg.setText(dataList.get(position).getLastMsg());
        cvh.unreadMessagesIco.setVisibility(View.GONE);
        Picasso.with(context).load(dataList.get(position).getProfileImg().replace("localhost", "10.0.2.2")).into(cvh.profileImg);
        cvh.element_chat.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("username", dataList.get(position).getUserName());
            bundle.putString("profile_img", dataList.get(position).getProfileImg());
            bundle.putString("chat_id", dataList.get(position).getChat_id());
            Navigation.findNavController(view).navigate(R.id.action_navigation_message_to_activeChat, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class chatViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout element_chat;
        TextView textoUsuario;

        TextView lastMsg;

        ImageView profileImg;

        LinearLayout unreadMessagesIco;

        TextView unreadMessagesNum;

        public chatViewHolder(View itemView) {
            super(itemView);
            element_chat = itemView.findViewById(R.id.element_chat);
            textoUsuario = itemView.findViewById(R.id.userName);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            profileImg = itemView.findViewById(R.id.profileImg);
            unreadMessagesIco = itemView.findViewById(R.id.unreadMessagesIco);
            unreadMessagesNum = itemView.findViewById(R.id.unreadMessagesNum);
        }
    }
}