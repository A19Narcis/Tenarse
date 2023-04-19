package com.example.tenarse.ui.newpost.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.home.HomeViewModel;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HashtagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> dataList;
    private Context context;

    private final int TYPE_IMAGE = 1;
    private final int TYPE_DOUBT = 2;
    private final int TYPE_VIDEO = 3;

    public HashtagAdapter(ArrayList<String> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_home_img, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        /*ListElementImg imgElement = (ListElementImg) dataList.get(position);
        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        imageViewHolder.username.setText(imgElement.getUsername());
        if (imgElement.getPost_img_text().equals("")){
            imageViewHolder.post_text.setVisibility(View.GONE);
        } else {
            imageViewHolder.post_text.setText(imgElement.getPost_img_text());
        }

        ImageView userImageView = imageViewHolder.userImageView;*/
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView userImageView;
        TextView username;
        TextView post_text;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_post_image);
            userImageView = itemView.findViewById(R.id.rv_userImage); //100px
            username = itemView.findViewById(R.id.rv_username);
            post_text = itemView.findViewById(R.id.rv_post_text);
        }
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;
        ImageView userImageView;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            title = itemView.findViewById(R.id.rv_title);
            description = itemView.findViewById(R.id.rv_description);
            userImageView = itemView.findViewById(R.id.rv_userImage);
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView post_text;
        ImageView userImageView;
        VideoView post_video;

        public VideoViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            post_text = itemView.findViewById(R.id.rv_post_text);
            userImageView = itemView.findViewById(R.id.rv_userImage);
            post_video = itemView.findViewById(R.id.rv_post_video);
        }
    }
}