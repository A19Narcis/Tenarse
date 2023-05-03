package com.example.tenarse.ui.user.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.profile.ProfileFragment;
import com.example.tenarse.ui.user.UserFragment;
import com.example.tenarse.ui.user.elements.ListElementDoubt;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.example.tenarse.ui.user.elements.ListElementVideo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MultiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;
    private final int TYPE_IMAGE = 1;
    private final int TYPE_DOUBT = 2;
    private final int TYPE_VIDEO = 3;

    private UserFragment mUserFragment;
    private ProfileFragment mProfileFragment;
    private Context context;

    public MultiAdapter(List<Object> dataList, Context context, UserFragment mUserFragment) {
        this.dataList = dataList;
        this.mUserFragment = mUserFragment;
        this.context = context;
    }

    public MultiAdapter(List<Object> dataList, Context context, ProfileFragment mProfileFragment) {
        this.dataList = dataList;
        this.mProfileFragment = mProfileFragment;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof ListElementImg) {
            return TYPE_IMAGE;
        } else if (dataList.get(position) instanceof ListElementDoubt) {
            return TYPE_DOUBT;
        }else if(dataList.get(position) instanceof ListElementVideo){
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
                view = inflater.inflate(R.layout.list_element_user_img, parent, false);
                return new ImageViewHolder(view);
            case TYPE_DOUBT:
                view = inflater.inflate(R.layout.list_element_user_doubt, parent, false);
                return new MultiAdapter.DoubtViewHolder(view);
            case TYPE_VIDEO:
                view = inflater.inflate(R.layout.list_element_user_video, parent, false);
                return new MultiAdapter.VideoViewHolder(view);
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
                Picasso.with(context).load(imgElement.getPost_img_url().replace("localhost", "10.0.2.2")).into(imageViewHolder.imageView);
                imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUserFragment != null){
                            mUserFragment.selectPost(imgElement.getPost_img_id(), v, imgElement.getUsername());
                        }

                        if (mProfileFragment != null) {
                            mProfileFragment.selectPost(imgElement.getPost_img_id(), v, imgElement.getUsername());
                        }
                    }
                });
                break;
            case TYPE_DOUBT:
                ListElementDoubt doubtElement = (ListElementDoubt) dataList.get(position);
                DoubtViewHolder doubtViewHolder = (DoubtViewHolder) holder;

                doubtViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUserFragment != null){
                            mUserFragment.selectPost(doubtElement.getDoubt_id(), v, doubtElement.getUsername());
                        }

                        if (mProfileFragment != null) {
                            mProfileFragment.selectPost(doubtElement.getDoubt_id(), v, doubtElement.getUsername());
                        }
                    }
                });

                doubtViewHolder.title.setText(doubtElement.getTitle());
                break;
            case TYPE_VIDEO:
                ListElementVideo videoElement = (ListElementVideo) dataList.get(position);
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;

                videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUserFragment != null){
                            mUserFragment.selectPost(videoElement.getPost_video_id(), v, videoElement.getUsername());
                        }

                        if (mProfileFragment != null) {
                            mProfileFragment.selectPost(videoElement.getPost_video_id(), v, videoElement.getUsername());
                        }
                    }
                });

                /* Cargar VIDEO */
                String videoPath = videoElement.post_video_url.replace("localhost", "10.0.2.2");
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

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.userFeedPost);
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
