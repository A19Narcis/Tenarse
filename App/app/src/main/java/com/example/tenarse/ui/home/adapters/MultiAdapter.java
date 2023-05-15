package com.example.tenarse.ui.home.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.home.HomeViewModel;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;
import com.example.tenarse.ui.home.elements.ListElementVideo;
import com.example.tenarse.ui.message.SharePostObject;
import com.example.tenarse.ui.message.adapters.ChatAdapter;
import com.example.tenarse.ui.message.adapters.ShareAdapter;
import com.example.tenarse.ui.message.chat.chatObject;
import com.example.tenarse.ui.search.posts.MyAsyncTaskGetPosts;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MultiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;
    private Context context;
    private HomeFragment mHomeFragment;
    ShareAdapter shareAdapter;
    private final int TYPE_IMAGE = 1;
    private final int TYPE_DOUBT = 2;
    private final int TYPE_VIDEO = 3;
    private ArrayList<SharePostObject> chatsList;
    private int currentItemPosition = 0;
    private int totalItemCount = 0;

    private GlobalDadesUser globalDadesUser;

    private JSONObject dadesUser;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        currentItemPosition = position;
        totalItemCount = getItemCount() - 1;

        if (currentItemPosition == totalItemCount){
            mHomeFragment.morePosts();
        }


        switch (holder.getItemViewType()) {
            case TYPE_IMAGE:
                ListElementImg imgElement = (ListElementImg) dataList.get(position);
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.username.setText(imgElement.getUsername());

                imageViewHolder.send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        globalDadesUser = GlobalDadesUser.getInstance();
                        dadesUser = globalDadesUser.getDadesUser();

                        chatsList = new ArrayList<>();
                        shareAdapter = new ShareAdapter(chatsList, context);

                        chatsList.clear();

                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.seguidores_dialog);

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                        if (windowManager != null) {
                            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
                            int screenWidth = displayMetrics.widthPixels;
                            int screenHeight = displayMetrics.heightPixels;

                            // Calcular el ancho deseado para el diálogo (la mitad de la pantalla)
                            int desiredWidth = (int) (screenWidth / 1.45f);
                            int desiredHeight = screenHeight / 3;

                            // Obtener la ventana del diálogo
                            Window window = dialog.getWindow();
                            if (window != null) {
                                // Establecer el ancho y alto personalizados
                                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                                params.copyFrom(window.getAttributes());
                                params.width = desiredWidth;
                                params.height = desiredHeight;

                                // Establecer la gravedad para centrar horizontalmente
                                params.gravity = Gravity.CENTER_HORIZONTAL;

                                window.setAttributes(params);

                                // Aplicar bordes redondeados al diálogo
                                int cornerRadius = 20; // Valor en píxeles, ajusta según tus necesidades
                                ShapeDrawable shapeDrawable = new ShapeDrawable();
                                shapeDrawable.getPaint().setColor(Color.WHITE); // Color del fondo del diálogo
                                shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
                                shapeDrawable.getPaint().setAntiAlias(true);
                                shapeDrawable.setShape(new RoundRectShape(
                                        new float[]{cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius},
                                        null,
                                        null));
                                window.setBackgroundDrawable(shapeDrawable);
                            }
                        }

                        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerViewSeguidores);
                        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

                        //Cargar seguidores /*IMAGEN PERFIL*/ - /*@USERNAME*/
                        String url = "http://10.0.2.2:3000/getAllMyChats";
                        JSONObject body = new JSONObject();
                        try {
                            body.put("_id", dadesUser.getString("_id"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        MyAsyncTaskGetPosts getInfoFollowers = new MyAsyncTaskGetPosts(url, body);
                        getInfoFollowers.execute();
                        String result = null;
                        try {
                            result = getInfoFollowers.get();

                            JSONArray followsArray = new JSONArray(result);

                            cargarChats(followsArray, dadesUser, imgElement.getId(), imgElement);

                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setAdapter(shareAdapter);

                        } catch (ExecutionException | InterruptedException | JSONException e) {
                            throw new RuntimeException(e);
                        }

                        dialog.show();
                    }
                });

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

                doubtViewHolder.sharePost.setOnClickListener(view -> {
                    animateButton(doubtViewHolder.sharePost);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Tenarse");
                    intent.putExtra(Intent.EXTRA_TEXT, "Mira esta publicacion de Tenarse: http://10.0.2.2:3000/app/publicacion_template?id=" + doubtElement.getId());
                    mHomeFragment.startActivity(Intent.createChooser(intent, "Comparte:"));
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

    private void cargarChats(JSONArray arrayChats, JSONObject dadesUsuari, String id, ListElementImg listElementImg) {
        try {
            for (int i = 0; i < arrayChats.length(); i++) {
                if (arrayChats.getJSONObject(i).getString("tipo").equals("chat")) {
                    JSONObject json = arrayChats.getJSONObject(i);
                    JSONArray participants = json.getJSONArray("participants");
                    String idFotoChat = null;
                    for (int j = 0; j < participants.length(); j++) {
                        if (!dadesUsuari.getString("_id").equals(participants.get(j))) {
                            idFotoChat = participants.get(j).toString();
                        }
                    }
                    String realUsername = getUsernameandImageFromID(idFotoChat);
                    JSONObject username_image = new JSONObject(realUsername);
                    JSONObject newUser = new JSONObject();
                    newUser.put("id", idFotoChat);
                    newUser.put("username", username_image.getString("username"));
                    System.out.println("NEWWWWUSEEEEER: "+ newUser);
                    chatsList.add(new SharePostObject(username_image.getString("username"), username_image.getString("url_img"),listElementImg.getId(), dadesUsuari.getString("_id"), arrayChats.getJSONObject(i).getString("_id")));
                    shareAdapter.notifyItemInserted(chatsList.size() - 1);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsernameandImageFromID(String idUser) {
        String url_selectUser = "http://10.0.2.2:3000/getUsernameAndImageFromID";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_user", idUser);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
        selectedUser.execute();
        String resultSearch = null;
        try {
            resultSearch = selectedUser.get();
            System.out.println(resultSearch);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resultSearch;
    }

    private void animateButton(ImageView sharePost) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(sharePost, "translationX", 0f, 10f, -10f, 0f);
        animator.setDuration(200);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
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

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView userImageView;
        TextView username;
        TextView post_text;
        ImageView likeImage;
        ImageView send_image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_post_image);
            userImageView = itemView.findViewById(R.id.rv_userImage); //100px
            username = itemView.findViewById(R.id.rv_username);
            post_text = itemView.findViewById(R.id.rv_post_text);
            likeImage = itemView.findViewById(R.id.like_image);
            send_image = itemView.findViewById(R.id.send_image);
        }
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;
        ImageView userImageView;
        ImageView likeImage;
        ImageView sharePost;


        public DoubtViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            title = itemView.findViewById(R.id.rv_title);
            description = itemView.findViewById(R.id.rv_description);
            userImageView = itemView.findViewById(R.id.rv_userImage);
            likeImage = itemView.findViewById(R.id.like_image);
            sharePost = itemView.findViewById(R.id.share_icon);
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