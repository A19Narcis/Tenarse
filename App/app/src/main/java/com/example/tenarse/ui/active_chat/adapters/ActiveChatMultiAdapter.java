package com.example.tenarse.ui.active_chat.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.active_chat.MessageObject;
import com.example.tenarse.ui.active_chat.PostObject;
import com.example.tenarse.ui.active_chat.activeChat;
import com.example.tenarse.ui.home.HomeViewModel;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ActiveChatMultiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private GlobalDadesUser globalDadesUser;

    private JSONObject dadesUser;
    private List<Object> dataList;
    private Context context;
    private activeChat ActiveChat;

    private final int TYPE_MESSAGE = 1;
    private final int TYPE_POST = 2;

    public ActiveChatMultiAdapter(List<Object> dataList, Context context, activeChat AChat) {
        this.dataList = dataList;
        this.context = context;
        this.ActiveChat = AChat;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof MessageObject) {
            return TYPE_MESSAGE;
        } else if (dataList.get(position) instanceof PostObject) {
            return TYPE_POST;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_MESSAGE:
                view = inflater.inflate(R.layout.list_element_normal_message, parent, false);
                return new MessageViewHolder(view);
            case TYPE_POST:
                view = inflater.inflate(R.layout.list_element_post_message, parent, false);
                return new PostViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        globalDadesUser = GlobalDadesUser.getInstance();
        dadesUser = globalDadesUser.getDadesUser();
        String id = null;
        try {
            id = dadesUser.getString("_id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        switch (holder.getItemViewType()) {
            case TYPE_MESSAGE:
                MessageObject messageElement = (MessageObject) dataList.get(position);
                MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
                messageViewHolder.usernameTxt.setText("@" + messageElement.getUserName());
                messageViewHolder.msgText.setText(messageElement.getMessage());
                ConstraintLayout.LayoutParams layoutParams =
                        (ConstraintLayout.LayoutParams) messageViewHolder.cardView.getLayoutParams();
                System.out.println("AQUI ESTA LA IDDDDDDDDDDDDDD: "+ id);
                if(messageElement.getIdEmitter().equals(id)){
                    // Eliminar la restricción layout_constraintEnd_toEndOf
                    layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
                    messageViewHolder.cardView.setLayoutParams(layoutParams);
                    messageViewHolder.cardView.setCardBackgroundColor(Color.BLACK);
                    messageViewHolder.msgText.setTextColor(Color.WHITE);
                }else{
                    // Eliminar la restricción layout_constraintEnd_toEndOf
                    layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
                    messageViewHolder.cardView.setLayoutParams(layoutParams);
                }
                break;
            case TYPE_POST:
                PostObject postElement = (PostObject) dataList.get(position);
                PostViewHolder postViewHolder = (PostViewHolder) holder;
                postViewHolder.usernameTxt.setText(postElement.getUserName());
                Picasso.with(context).load(postElement.getUrl_img().replace("localhost","10.0.2.2")).into(postViewHolder.imagePost);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout constraintLayout;
        CardView cardView;
        TextView usernameTxt;
        TextView msgText;
        public MessageViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.chat_element_direction);
            cardView = itemView.findViewById(R.id.cardView);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            msgText = itemView.findViewById(R.id.txtMsg);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        CardView cardView;
        TextView usernameTxt;
        ImageView imagePost;
        public PostViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.chat_element_direction);
            cardView = itemView.findViewById(R.id.cardView);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            imagePost = itemView.findViewById(R.id.imagePost);
        }
    }
}