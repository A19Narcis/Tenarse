package com.example.tenarse.ui.home.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.home.elements.ListElementVideo;

import java.util.List;

public class ListElementVideoAdapter extends RecyclerView.Adapter<ListElementVideoAdapter.ViewHolder> {
    private List<ListElementVideo> listElementVideos;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListElementVideoAdapter(List<ListElementVideo> itemList, Context context){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.listElementVideos = itemList;
    }

    @Override
    public int getItemCount() {
        return listElementVideos.size();
    }


    @Override
    public ListElementVideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_home_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListElementVideoAdapter.ViewHolder holder, int position) {
        holder.bindData(listElementVideos.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Items que tiene cada elemento de la RecyclerView
        public ImageView user_image;
        public TextView username;
        public VideoView post_video;

        public ViewHolder(View itemView){
            super(itemView);
            user_image = itemView.findViewById(R.id.rv_userImage);
            username = itemView.findViewById(R.id.rv_username);
            post_video = (VideoView) itemView.findViewById(R.id.rv_post_video);
        }

        public void bindData(ListElementVideo listElementVideo){
            /* Modificar las imagenes de perfil / post i tambien el texto de username */
            username.setText(listElementVideo.getUsername());

            /*VIDEO SETTINGS*/
            MediaController mediaController = new MediaController(context);
            post_video.setMediaController(mediaController);
            mediaController.setAnchorView(post_video);

            Uri videoUri = Uri.parse("https://www.youtube.com/watch?v=iiJQPZg9DqU&ab_channel=zIXAutomatizaci%C3%B3n");
            post_video.setVideoURI(videoUri);
            post_video.requestFocus();
            post_video.start();


        }
    }
}
