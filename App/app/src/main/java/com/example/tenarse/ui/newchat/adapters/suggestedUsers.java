package com.example.tenarse.ui.newchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.message.adapters.ChatAdapter;
import com.example.tenarse.ui.message.chat.chatObject;

import java.util.ArrayList;

public class suggestedUsers extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<chatObject> dataList;
    private Context context;

    public suggestedUsers(ArrayList<chatObject> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_chat, parent, false);
        return new ChatAdapter.chatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatAdapter.chatViewHolder cvh = (ChatAdapter.chatViewHolder) holder;
        cvh.textoUsuario.setText(dataList.get(position).getUserName());
        cvh.lastMsg.setText(dataList.get(position).getLastMsg());
        //cvh.profileImg.setImageBitmap(dataList.get(position).getProfileImg());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder {
        TextView textoUsuario;

        TextView lastMsg;

        ImageView profileImg;

        LinearLayout unreadMessagesIco;

        TextView unreadMessagesNum;

        public usersViewHolder(View itemView) {
            super(itemView);
            textoUsuario = itemView.findViewById(R.id.userName);
            lastMsg = itemView.findViewById(R.id.lastMsg);
            profileImg = itemView.findViewById(R.id.profileImg);
            unreadMessagesIco = itemView.findViewById(R.id.unreadMessagesIco);
            unreadMessagesNum = itemView.findViewById(R.id.unreadMessagesNum);
        }
    }
}
