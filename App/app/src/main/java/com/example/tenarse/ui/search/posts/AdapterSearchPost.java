package com.example.tenarse.ui.search.posts;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSearchPost extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;

    private final int TYPE_IMAGE = 1;
    private final int TYPE_VIDEO = 2;

    private SearchPostFragment mSearchPostFragment;
    private Context context;

    public AdapterSearchPost(List<Object> dataList, Context context, SearchPostFragment mSearchPostFragment) {
        this.dataList = dataList;
        this.mSearchPostFragment = mSearchPostFragment;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof ListElementImg) {
            return TYPE_IMAGE;
        } else if(dataList.get(position) instanceof ListElementVideo){
            return TYPE_VIDEO;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_IMAGE:
                view = inflater.inflate(R.layout.list_element_user_img_search, parent, false);
                return new AdapterSearchPost.ImageViewHolder(view);
            case TYPE_VIDEO:
                view = inflater.inflate(R.layout.list_element_user_video_search, parent, false);
                return new AdapterSearchPost.VideoViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_IMAGE:
                ListElementImg imgElement = (ListElementImg) dataList.get(position);
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                Picasso.with(context).load(imgElement.getPost_img_url().replace("localhost", "212.227.40.235")).into(imageViewHolder.imageView);
                imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSearchPostFragment != null){
                            mSearchPostFragment.selectPost(imgElement.getPost_img_id(), v, imgElement.getUsername(), imgElement.getUser_img_url());
                        }
                    }
                });
                break;
            case TYPE_VIDEO:
                ListElementVideo videoElement = (ListElementVideo) dataList.get(position);
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

                videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSearchPostFragment != null){
                            mSearchPostFragment.selectPost(videoElement.getPost_video_id(), v, videoElement.getUsername(), videoElement.getUser_img_url());
                        }
                    }
                });

                /* Cargar VIDEO */
                String videoPath = videoElement.post_video_url;
                /*Uri uri = Uri.parse(videoPath);*/
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoPath);
                Bitmap bitmap = retriever.getFrameAtTime(0);
                videoViewHolder.miniatura.setImageBitmap(bitmap);

                break;
        }
    }


    public void setList(List<Object> nuevaListaDatos) {
        this.dataList = nuevaListaDatos;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.userFeedPost);
        }
    }


    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView miniatura;

        public VideoViewHolder(View itemView) {
            super(itemView);
            miniatura = itemView.findViewById(R.id.userFeedPost);
        }
    }


}
