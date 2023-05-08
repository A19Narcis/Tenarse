package com.example.tenarse.ui.home.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.home.HomeViewModel;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MultiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;
    private Context context;
    private HomeFragment mHomeFragment;

    private final int TYPE_IMAGE = 1;
    private final int TYPE_DOUBT = 2;
    private final int TYPE_VIDEO = 3;

    public MultiAdapter(List<Object> dataList, Context context, HomeFragment mHomeFragment) {
        this.dataList = dataList;
        this.context = context;
        this.mHomeFragment = mHomeFragment;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof ListElementImg) {
            return TYPE_IMAGE;
        } else if (dataList.get(position) instanceof ListElementDoubt) {
            return TYPE_DOUBT;
        } else if (dataList.get(position) instanceof  ListElementVideo){
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
                view = inflater.inflate(R.layout.list_element_home_img, parent, false);
                return new ImageViewHolder(view);
            case TYPE_DOUBT:
                view = inflater.inflate(R.layout.list_element_home_doubt, parent, false);
                return new DoubtViewHolder(view);
            case TYPE_VIDEO:
                view = inflater.inflate(R.layout.list_element_home_video, parent, false);
                return new VideoViewHolder(view);
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
                imageViewHolder.username.setText(imgElement.getUsername());

                imageViewHolder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Aquí
                        mHomeFragment.selectUser(imageViewHolder.username.getText().toString(), v);
                    }
                });

                if (imgElement.getPost_img_text().equals("")){
                    imageViewHolder.post_text.setVisibility(View.GONE);
                } else {
                    imageViewHolder.post_text.setText(imgElement.getPost_img_text());
                }

                /* Cargar imagen con PISCASSO */
                String urlImagen = imgElement.getPost_img_url().replace("localhost", "10.0.2.2");
                Picasso.with(context).load(urlImagen).into(imageViewHolder.imageView);

                /* Cargar USER IMAGE BITMAT Hilo */
                String urlUserImg = imgElement.getUser_img_url().replace("localhost", "10.0.2.2");
                ImageView userImageView = imageViewHolder.userImageView;
                new HomeViewModel.DownloadImageTask(userImageView).execute(urlUserImg);

                imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHomeFragment.selectPost(imgElement.getId(), v, imgElement.getUsername(), imgElement.getUser_img_url());
                    }
                });

                if (!imgElement.isLiked()){
                    imageViewHolder.likeImage.setImageResource(R.drawable.no_like);
                } else if (imgElement.isLiked()) {
                    imageViewHolder.likeImage.setImageResource(R.drawable.like);
                }

                imageViewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!imgElement.isLiked()){
                            mHomeFragment.addLike(imgElement.getId());
                            imageViewHolder.likeImage.setImageResource(R.drawable.like);
                            imgElement.setLiked(true);
                        } else if (imgElement.isLiked()) {
                            mHomeFragment.removeLike(imgElement.getId());
                            imageViewHolder.likeImage.setImageResource(R.drawable.no_like);
                            imgElement.setLiked(false);
                        }
                    }
                });


                break;
            case TYPE_DOUBT:
                ListElementDoubt doubtElement = (ListElementDoubt) dataList.get(position);
                DoubtViewHolder doubtViewHolder = (DoubtViewHolder) holder;
                doubtViewHolder.username.setText(doubtElement.getUsername());
                Picasso.with(context).load(doubtElement.getUser_img_url().replace("localhost", "10.0.2.2")).into(doubtViewHolder.userImageView);


                doubtViewHolder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Aquí
                        mHomeFragment.selectUser(doubtViewHolder.username.getText().toString(), v);
                    }
                });

                doubtViewHolder.title.setText(doubtElement.getTitle());
                doubtViewHolder.description.setText(doubtElement.getDescription());

                /* Cargar USER IMAGE BITMAT Hilo */
                /*String urlUserDoubt = doubtElement.getUser_img_url().replace("localhost", "10.0.2.2");
                ImageView userImageViewDoubt = doubtViewHolder.userImageView;
                new HomeViewModel.DownloadImageTask(userImageViewDoubt).execute(urlUserDoubt);*/

                doubtViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHomeFragment.selectPost(doubtElement.getId(), v, doubtElement.getUsername(), doubtElement.getUser_img_url());
                    }
                });


                if (!doubtElement.isLiked()){
                    doubtViewHolder.likeImage.setImageResource(R.drawable.no_like);
                } else if (doubtElement.isLiked()) {
                    doubtViewHolder.likeImage.setImageResource(R.drawable.like);
                }

                doubtViewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!doubtElement.isLiked()){
                            mHomeFragment.addLike(doubtElement.getId());
                            doubtViewHolder.likeImage.setImageResource(R.drawable.like);
                            doubtElement.setLiked(true);
                        } else if (doubtElement.isLiked()) {
                            mHomeFragment.removeLike(doubtElement.getId());
                            doubtViewHolder.likeImage.setImageResource(R.drawable.no_like);
                            doubtElement.setLiked(false);
                        }
                    }
                });

                break;

            case TYPE_VIDEO:
                ListElementVideo videoElement = (ListElementVideo) dataList.get(position);
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
                videoViewHolder.username.setText(videoElement.getUsername());
                String urImg = videoElement.getUser_img_url().replace("localhost", "10.0.2.2");
                Picasso.with(context).load(urImg).into(videoViewHolder.userImageView);

                videoViewHolder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Aquí
                        mHomeFragment.selectUser(videoViewHolder.username.getText().toString(), v);
                    }
                });

                if (videoElement.getPost_text().equals("")){
                    videoViewHolder.post_text.setVisibility(View.GONE);
                } else {
                    videoViewHolder.post_text.setText(videoElement.getPost_text());
                }

                /* Cargar VIDEO */

                verProgressBar(videoViewHolder);

                String videoPath = videoElement.getPost_video_url().replace("localhost", "10.0.2.2");
                /*Uri uri = Uri.parse(videoPath);
                videoViewHolder.post_video.setVideoURI(uri);*/
                videoViewHolder.post_video.setVideoPath(videoPath);
                MediaController mediaController = new MediaController(context);
                videoViewHolder.post_video.setMediaController(null);
                mediaController.setAnchorView(videoViewHolder.post_video);
                videoViewHolder.post_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        MultiAdapter.this.ocultarProgressBar(videoViewHolder);
                        mp.setLooping(true);
                        mp.start();
                    }
                });

                videoViewHolder.post_video.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        System.out.println("Error al reproducir el video: " + what + ", " + extra);
                        videoViewHolder.post_video.setVideoURI(Uri.parse(videoElement.getPost_video_url().replace("localhost", "10.0.2.2")));
                        videoViewHolder.post_video.setOnPreparedListener(mp1 -> {
                            MultiAdapter.this.ocultarProgressBar(videoViewHolder);
                            mp1.setLooping(true);
                            mp1.start();
                        });
                        return true;
                    }
                });


                if (!videoElement.isLiked()){
                    videoViewHolder.likeImage.setImageResource(R.drawable.no_like);
                } else if (videoElement.isLiked()) {
                    videoViewHolder.likeImage.setImageResource(R.drawable.like);
                }

                videoViewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!videoElement.isLiked()){
                            videoViewHolder.likeImage.setImageResource(R.drawable.like);
                            videoElement.setLiked(true);
                            mHomeFragment.addLike(videoElement.getId());
                        } else if (videoElement.isLiked()) {
                            mHomeFragment.removeLike(videoElement.getId());
                            videoViewHolder.likeImage.setImageResource(R.drawable.no_like);
                            videoElement.setLiked(false);
                        }
                    }
                });

                videoViewHolder.post_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(videoViewHolder.post_video.isPlaying()){
                            videoViewHolder.post_video.pause();
                        }else{
                            videoViewHolder.post_video.start();
                        }
                    }
                });

                videoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mHomeFragment.selectPost(videoElement.getId(), v, videoElement.getUsername(), videoElement.getUser_img_url());
                    }
                });

                break;
        }
    }

    private void verProgressBar(VideoViewHolder videoViewHolder) {
        videoViewHolder.progressBar.setVisibility(View.VISIBLE);
    }

    private void ocultarProgressBar(VideoViewHolder videoViewHolder) {
        videoViewHolder.progressBar.setVisibility(View.GONE);
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
        ImageView likeImage;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_post_image);
            userImageView = itemView.findViewById(R.id.rv_userImage); //100px
            username = itemView.findViewById(R.id.rv_username);
            post_text = itemView.findViewById(R.id.rv_post_text);
            likeImage = itemView.findViewById(R.id.like_image);
        }
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;
        ImageView userImageView;
        ImageView likeImage;


        public DoubtViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            title = itemView.findViewById(R.id.rv_title);
            description = itemView.findViewById(R.id.rv_description);
            userImageView = itemView.findViewById(R.id.rv_userImage);
            likeImage = itemView.findViewById(R.id.like_image);
        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView post_text;
        ImageView userImageView;
        VideoView post_video;
        ImageView likeImage;

        ProgressBar progressBar;

        public VideoViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            post_text = itemView.findViewById(R.id.rv_post_text);
            userImageView = itemView.findViewById(R.id.rv_userImage);
            post_video = itemView.findViewById(R.id.rv_post_video);
            likeImage = itemView.findViewById(R.id.like_image);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}