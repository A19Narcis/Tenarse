package com.example.tenarse.ui.active_chat.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.active_chat.MessageObject;
import com.example.tenarse.ui.active_chat.MyMessageObject;
import com.example.tenarse.ui.active_chat.MyPostObject;
import com.example.tenarse.ui.active_chat.PostObject;
import com.example.tenarse.ui.active_chat.activeChat;
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

    private final int TYPE_MY_MESSAGE = 2;
    private final int TYPE_POST = 3;

    private final int TYPE_MY_POST = 4;

    public ActiveChatMultiAdapter(List<Object> dataList, Context context, activeChat AChat) {
        this.dataList = dataList;
        this.context = context;
        this.ActiveChat = AChat;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof MessageObject) {
            return TYPE_MESSAGE;
        }else if (dataList.get(position) instanceof MyMessageObject) {
            return TYPE_MY_MESSAGE;
        }
        else if (dataList.get(position) instanceof PostObject) {
            return TYPE_POST;
        }
        else if (dataList.get(position) instanceof MyPostObject) {
            return TYPE_MY_POST;
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
            case TYPE_MY_MESSAGE:
                view = inflater.inflate(R.layout.list_element_normal_message_my, parent, false);
                return new MyMessageViewHolder(view);
            case TYPE_POST:
                view = inflater.inflate(R.layout.list_element_post_message, parent, false);
                return new PostViewHolder(view);
            case TYPE_MY_POST:
                view = inflater.inflate(R.layout.list_element_my_post_message, parent, false);
                return new MyPostViewHolder(view);
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
                if(messageElement.getMessage().equals("/*Publicación eliminada*/")){
                    Typeface currentTypeface = messageViewHolder.msgText.getTypeface();
                    messageViewHolder.msgText.setTypeface(currentTypeface, Typeface.BOLD);
                    messageElement.setMessage("Publicación eliminada");
                }
                messageViewHolder.msgText.setText(messageElement.getMessage());
                break;
            case TYPE_MY_MESSAGE:
                MyMessageObject myMessageElement = (MyMessageObject) dataList.get(position);
                MyMessageViewHolder myMessageViewHolder = (MyMessageViewHolder) holder;
                myMessageViewHolder.usernameTxt.setText("@" + myMessageElement.getUserName());
                if(myMessageElement.getMessage().equals("/*Publicación eliminada*/")){
                    Typeface currentTypeface = myMessageViewHolder.msgText.getTypeface();
                    myMessageViewHolder.msgText.setTypeface(currentTypeface, Typeface.BOLD);
                    myMessageElement.setMessage("Publicación eliminada");
                }
                myMessageViewHolder.msgText.setText(myMessageElement.getMessage());
                break;
            case TYPE_POST:
                PostObject postElement = (PostObject) dataList.get(position);
                PostViewHolder postViewHolder = (PostViewHolder) holder;
                postViewHolder.usernameTxt.setText("@" + postElement.getEmisor_username());

                if (postElement.getPost_image().length() > 0){
                    System.out.println("Post element: "+ postElement.getPost_image());
                    postViewHolder.imagePost.setVisibility(View.VISIBLE);
                    postViewHolder.textPost.setVisibility(View.GONE);
                    if (postElement.getPost_image().contains(".mp4")){
                        String videoPath = postElement.getPost_image().replace("localhost", "212.227.40.235");
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(videoPath);
                        Bitmap bitmap = retriever.getFrameAtTime(0);
                        postViewHolder.imagePost.setImageBitmap(bitmap);
                    } else {
                        Picasso.with(context).load(postElement.getPost_image().replace("localhost", "212.227.40.235")).into(postViewHolder.imagePost);
                    }
                } else {
                    postViewHolder.textPost.setText(postElement.getPost_text());
                    postViewHolder.imagePost.setVisibility(View.GONE);
                    postViewHolder.textPost.setVisibility(View.VISIBLE);
                }
                Picasso.with(context).load(postElement.getOwner_post_image().replace("localhost", "212.227.40.235")).into(postViewHolder.postOwnerImage);

                postViewHolder.cardView.setOnClickListener(view -> {
                    this.ActiveChat.selectPost(postElement.getId_post(), view, postElement.getUsername(), postElement.getOwner_post_image());
                });

                break;
            case TYPE_MY_POST:
                MyPostObject myPostElement = (MyPostObject) dataList.get(position);
                MyPostViewHolder mypostViewHolder = (MyPostViewHolder) holder;
                mypostViewHolder.usernameTxt.setText("@" + myPostElement.getEmisor_username());
                if (myPostElement.getPost_image().length() > 0){
                    mypostViewHolder.imagePost.setVisibility(View.VISIBLE);
                    mypostViewHolder.textPost.setVisibility(View.GONE);
                    if (myPostElement.getPost_image().contains(".mp4")){
                        String videoPath = myPostElement.getPost_image().replace("localhost", "212.227.40.235");
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(videoPath);
                        Bitmap bitmap = retriever.getFrameAtTime(0);
                        mypostViewHolder.imagePost.setImageBitmap(bitmap);
                    } else {
                        Picasso.with(context).load(myPostElement.getPost_image().replace("localhost", "212.227.40.235")).into(mypostViewHolder.imagePost);
                    }
                } else {
                    mypostViewHolder.textPost.setText(myPostElement.getPost_text());
                    mypostViewHolder.imagePost.setVisibility(View.GONE);
                    mypostViewHolder.textPost.setVisibility(View.VISIBLE);
                }
                Picasso.with(context).load(myPostElement.getOwner_post_image().replace("localhost", "212.227.40.235")).into(mypostViewHolder.postOwnerImage);
                mypostViewHolder.cardView.setOnClickListener(view -> {
                    this.ActiveChat.selectPost(myPostElement.getId_post(), view, myPostElement.getUsername(), myPostElement.getOwner_post_image());
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView usernameTxt;
        TextView msgText;
        public MessageViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.chat_element_direction);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            msgText = itemView.findViewById(R.id.txtMsg);
        }
    }

    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        TextView usernameTxt;
        TextView msgText;
        public MyMessageViewHolder(View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.chat_element_direction);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            msgText = itemView.findViewById(R.id.txtMsg);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView usernameTxt;
        ImageView imagePost;
        ImageView postOwnerImage;
        TextView textPost;

        public PostViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.mainCardView);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            imagePost = itemView.findViewById(R.id.imagePost);
            postOwnerImage = itemView.findViewById(R.id.ProfileImg);
            textPost = itemView.findViewById(R.id.textPost);
        }
    }

    public static class MyPostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView usernameTxt;
        ImageView imagePost;
        ImageView postOwnerImage;
        TextView textPost;


        public MyPostViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.mainCardView);
            usernameTxt = itemView.findViewById(R.id.usernameTxt);
            imagePost = itemView.findViewById(R.id.imagePost);
            postOwnerImage = itemView.findViewById(R.id.ProfileImg);
            textPost = itemView.findViewById(R.id.textPost);
        }
    }
}