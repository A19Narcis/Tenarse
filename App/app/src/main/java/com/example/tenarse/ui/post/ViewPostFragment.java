package com.example.tenarse.ui.post;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Toast;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentViewPostBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetSinglePost;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskLikes;
import com.example.tenarse.ui.message.SharePostObject;
import com.example.tenarse.ui.message.adapters.ShareAdapter;
import com.example.tenarse.ui.post.adapters.AdapterComentarios;
import com.example.tenarse.ui.post.asynctask.MyAsyncTaskComment;
import com.example.tenarse.ui.post.asynctask.MyAsyncTaskDeletePost;
import com.example.tenarse.ui.post.elements.Comentario;
import com.example.tenarse.ui.search.posts.MyAsyncTaskGetPosts;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewPostFragment extends Fragment {

    private FragmentViewPostBinding binding;
    private String infoPost;

    private List<Comentario> comentarioList;
    private AdapterComentarios adapterComentarios;
    private int numeroComentarios = 0;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

    String fragmentAnterior = "";

    private boolean isLiked;

    private ArrayList<SharePostObject> chatsList;
    ShareAdapter shareAdapter;
    private String usernamePost;
    private String urlImg;
    private JSONObject dadesPost;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentViewPostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        comentarioList = new ArrayList<>();
        adapterComentarios = new AdapterComentarios(comentarioList, getContext());

        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });


        String originFragment = "";
        Bundle args = getArguments();
        if (args != null){
            infoPost = args.getString("infoPost");
            fragmentAnterior = args.getString("fragment");
            originFragment = args.getString("origin");
            isLiked = args.getBoolean("isLiked");
            usernamePost = args.getString("usernamePost");
            urlImg = args.getString("url_img");
        }



        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    refreshViewPostInfo(dadesPost.getString("_id"));
                    refreshViewPostInfoComments(dadesPost.getString("_id"), true);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        binding.swipeRefreshLayout.setDistanceToTriggerSync((int) (180 * getResources().getDisplayMetrics().density));

        binding.shareIcon.setOnClickListener(view -> {
                animateButton(binding.shareIcon);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Tenarse");
            try {
                intent.putExtra(Intent.EXTRA_TEXT, "Mira esta publicacion de Tenarse: https://tenarse.online/app/publicacion_template?id=" + dadesPost.getString("_id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            startActivity(Intent.createChooser(intent, "Comparte:"));
        });

        try {
            dadesPost = new JSONObject(infoPost);
            binding.rvUsername.setText(usernamePost);
            String userImg = urlImg;
            Picasso.with(getContext()).invalidate(userImg);
            Picasso.with(getContext()).load(userImg.replace("https://tenarse.online", "http://212.227.40.235")).into(binding.rvUserImage);

            //Veure l'icone de borrar 'post' si es teu el post
            if (!dadesUsuari.getString("_id").equals(dadesPost.getString("owner")) || !originFragment.equals("perfil")){
                binding.removeButton.setVisibility(View.GONE);
            } else {
                binding.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View viewButton) {
                        //Esborrar post
                        AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(getActivity());
                        alertaLogOut.setTitle("Eliminar publicación");
                        alertaLogOut.setMessage("¿Quieres eliminar esta publicación?");
                        alertaLogOut.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = "http://212.227.40.235:3000/deletePost";
                                JSONObject body = new JSONObject();
                                try {
                                    body.put("id_post", dadesPost.getString("_id"));
                                    body.put("user", dadesUsuari.getString("username"));

                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                MyAsyncTaskDeletePost deletePost = new MyAsyncTaskDeletePost(url, body);
                                deletePost.execute();
                                String resultDelete = null;
                                try {
                                    resultDelete = deletePost.get();
                                } catch (ExecutionException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }

                                Navigation.findNavController(viewButton).popBackStack();
                                Toast toast = Toast.makeText(getContext(), "Publicación eliminada", Toast.LENGTH_SHORT);
                                toast.setText("Publicación eliminada");
                                toast.show();
                            }
                        });
                        alertaLogOut.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Es queda a la pantalla
                            }
                        });
                        alertaLogOut.show();
                    }
                });
            }




            if (dadesPost.getString("tipus").equals("doubt")){
                binding.rvPostImage.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.rvTitle.setText(dadesPost.getString("titol"));
                binding.rvPostText.setText(dadesPost.getString("text"));
                ViewGroup.LayoutParams params = binding.cardViewRvVideo.getLayoutParams();
                params.height = 0;
                binding.cardViewRvVideo.setLayoutParams(params);
                ViewGroup.LayoutParams params_video = binding.rvPostVideo.getLayoutParams();
                params_video.height = 0;
                binding.rvPostVideo.setLayoutParams(params_video);
            } else if (dadesPost.getString("tipus").equals("image")){
                binding.rvTitle.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                ViewGroup.LayoutParams params = binding.cardViewRvVideo.getLayoutParams();
                params.height = 0;
                binding.cardViewRvVideo.setLayoutParams(params);
                ViewGroup.LayoutParams params_video = binding.rvPostVideo.getLayoutParams();
                params_video.height = 0;
                binding.rvPostVideo.setLayoutParams(params_video);
                binding.rvPostText.setText(dadesPost.getString("text"));
                Picasso.with(getContext())
                        .load(dadesPost.getString("url_img").replace("https://tenarse.online", "http://212.227.40.235"))
                        .into(binding.rvPostImage);
            } else if (dadesPost.getString("tipus").equals("video")){
                binding.rvTitle.setVisibility(View.GONE);
                binding.rvPostImage.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                String videoPath = dadesPost.getString("url_video").replace("https://tenarse.online", "http://212.227.40.235");
                binding.rvPostVideo.setVideoPath(videoPath);
                binding.rvPostText.setText(dadesPost.getString("text"));
                MediaController mediaController = new MediaController(getContext());
                binding.rvPostVideo.setMediaController(null);
                mediaController.setAnchorView(binding.rvPostVideo);
                binding.rvPostVideo.setOnPreparedListener(mp -> {
                    binding.progressBar.setVisibility(View.GONE);
                    mp.setLooping(true);
                    mp.start();
                });

                binding.rvPostVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        try {
                            binding.rvPostVideo.setVideoURI(Uri.parse(dadesPost.getString("url_video")));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        binding.rvPostVideo.setOnPreparedListener(mp1 -> {
                            binding.progressBar.setVisibility(View.GONE);
                            mp1.setLooping(true);
                            mp1.start();
                        });
                        return true;
                    }
                });
            }


            int numero_likes = dadesPost.getJSONArray("likes").length();
            if (numero_likes >= 10000 && numero_likes < 999950) {
                String likesString = formatLikes10(numero_likes);
                binding.numeroLikes.setText(likesString);
            } else if (numero_likes >= 999950){
                String likesString_100 = formatLikes100(numero_likes);
                binding.numeroLikes.setText(likesString_100);
            } else {
                binding.numeroLikes.setText(String.valueOf(numero_likes));
            }

            numeroComentarios = dadesPost.getJSONArray("comentaris").length();
            binding.numeroComentarios.setText("Comentarios (" + numeroComentarios + ")");

            binding.subirComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((binding.editTextComentario.getText().length() > 0 && binding.editTextComentario.getText().length() <= 100) || (binding.editTextComentario.getText().toString().replace(" ", "").length() > 0 && binding.editTextComentario.getText().toString().replace(" ", "").length() <= 100)){
                        String urlUploadComment = "http://212.227.40.235:3000/addNewComment";
                        JSONObject commentBody = new JSONObject();
                        JSONObject innerComentari = new JSONObject();
                        try {
                            innerComentari.put("user", dadesUsuari.getString("_id"));
                            innerComentari.put("coment_text", binding.editTextComentario.getText().toString());
                            commentBody.put("id_publi", dadesPost.getString("_id"));
                            commentBody.put("comentari", innerComentari);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                        MyAsyncTaskComment addNewComment = new MyAsyncTaskComment(urlUploadComment, commentBody);
                        addNewComment.execute();
                        String resultAddComment = null;
                        try {
                            resultAddComment = addNewComment.get();
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        try {
                            refreshViewPostInfoComments(dadesPost.getString("_id"), false);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        binding.editTextComentario.setText("");
                    }
                }
            });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.sendImage.setOnClickListener(view -> {
            verVentanaChats();
        });

        binding.editTextComentario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textNumChars.setText(s.length() + "/255");
                if (s.length() > 255){
                    binding.textNumChars.setTextColor(Color.RED);
                } else if (s.length() <= 255){
                    binding.textNumChars.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.recyclerViewComentarios.setHasFixedSize(true);
        binding.recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewComentarios.setAdapter(adapterComentarios);

        try {
            if (refreshViewPostInfo(dadesPost.getString("_id"))){
                binding.likeImage.setImageResource(R.drawable.like);
            } else {
                binding.likeImage.setImageResource(R.drawable.no_like);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (refreshViewPostInfo(dadesPost.getString("_id"))){
                        try {
                            removeLike(dadesPost.getString("_id"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        binding.likeImage.setImageResource(R.drawable.no_like);
                        isLiked = false;
                    } else {
                        try {
                            addNewLike(dadesPost.getString("_id"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        binding.likeImage.setImageResource(R.drawable.like);
                        isLiked = true;
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            refreshViewPostInfoComments(dadesPost.getString("_id"), true);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        binding.sendImage.setOnClickListener(view -> {
            animateButton(binding.sendImage);
            globalDadesUser = GlobalDadesUser.getInstance();
            globalDadesUser.getDadesUser();

            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.seguidores_dialog);

            MainActivity mainActivity = (MainActivity) getActivity();

            chatsList = new ArrayList<>();
            shareAdapter = new ShareAdapter(chatsList, getContext(), mainActivity, dialog);

            chatsList.clear();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
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
            String url = "http://212.227.40.235:3000/getAllMyChats";
            JSONObject body = new JSONObject();
            try {
                body.put("_id", globalDadesUser.getDadesUser().getString("_id"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            MyAsyncTaskGetPosts getInfoFollowers = new MyAsyncTaskGetPosts(url, body);
            getInfoFollowers.execute();
            String result = null;
            try {
                result = getInfoFollowers.get();

                JSONArray followsArray = new JSONArray(result);

                cargarChats(followsArray, globalDadesUser.getDadesUser(), dadesPost.getString("_id"));

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(shareAdapter);

            } catch (ExecutionException | InterruptedException | JSONException e) {
                throw new RuntimeException(e);
            }

            dialog.show();
        });

        return root;
    }

    private void verVentanaChats() {
        animateButton(binding.sendImage);
        globalDadesUser = GlobalDadesUser.getInstance();

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.seguidores_dialog);

        MainActivity mainActivity = (MainActivity) getActivity();

        chatsList = new ArrayList<>();
        shareAdapter = new ShareAdapter(chatsList, getContext(), mainActivity, dialog);

        chatsList.clear();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
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
        String url = "http://212.227.40.235:3000/getAllMyChats";
        JSONObject body = new JSONObject();
        try {
            body.put("_id", globalDadesUser.getDadesUser().getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskGetPosts getInfoFollowers = new MyAsyncTaskGetPosts(url, body);
        getInfoFollowers.execute();
        String result = null;
        try {
            result = getInfoFollowers.get();

            JSONArray followsArray = new JSONArray(result);

            cargarChats(followsArray, globalDadesUser.getDadesUser(), dadesPost.getString("_id"));

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(shareAdapter);

        } catch (ExecutionException | InterruptedException | JSONException e) {
            throw new RuntimeException(e);
        }

        dialog.show();
    }


    private void cargarChats(JSONArray arrayChats, JSONObject dadesUsuari, String id) {
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
                    chatsList.add(new SharePostObject(username_image.getString("username"), username_image.getString("url_img"), id, dadesUsuari.getString("_id"), arrayChats.getJSONObject(i).getString("_id")));
                    shareAdapter.notifyItemInserted(chatsList.size() - 1);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsernameandImageFromID(String idUser) {
        String url_selectUser = "http://212.227.40.235:3000/getUsernameAndImageFromID";
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


    private void refreshViewPostInfoComments(String id, boolean refreshed) {
        String url_selectPost = "http://212.227.40.235:3000/getSelectedPost/" + id;
        MyAsyncTaskGetSinglePost getSinglePost = new MyAsyncTaskGetSinglePost(url_selectPost);
        getSinglePost.execute();
        String resultSinglePost = null;
        try {
            resultSinglePost = getSinglePost.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        JSONObject new_dadesPost = null;
        try {
            new_dadesPost = new JSONObject(resultSinglePost);

            /*COMENTARIOS*/
            int numeroComentariosAntes = comentarioList.size();

            binding.numeroComentarios.setText("Comentarios (" + new_dadesPost.getJSONArray("comentaris").length() + ")");

            if (refreshed){
                for (int i = new_dadesPost.getJSONArray("comentaris").length(); i > numeroComentariosAntes; i--) {
                    JSONObject comentarioNuevo = (JSONObject) new_dadesPost.getJSONArray("comentaris").get(i-1);
                    //SACAR USERNAME
                    String realUsername = getUsernameFromID(comentarioNuevo);
                    JSONObject dadesRealUsername = new JSONObject(realUsername);
                    comentarioList.add(new Comentario(dadesRealUsername.getString("url_img"), dadesRealUsername.getString("username"), comentarioNuevo.getString("coment_text")));
                    adapterComentarios.notifyItemInserted(comentarioList.size());
                    binding.recyclerViewComentarios.requestLayout();
                }
            } else {
                for (int i = new_dadesPost.getJSONArray("comentaris").length(); i > numeroComentariosAntes; i--) {
                    JSONObject comentarioNuevo = (JSONObject) new_dadesPost.getJSONArray("comentaris").get(i-1);
                    //SACAR USERNAME
                    String realUsername = getUsernameFromID(comentarioNuevo);
                    JSONObject dadesRealUsername = new JSONObject(realUsername);
                    comentarioList.add(0, new Comentario(dadesRealUsername.getString("url_img"), dadesRealUsername.getString("username"), comentarioNuevo.getString("coment_text")));
                    adapterComentarios.notifyItemInserted(0);
                    binding.recyclerViewComentarios.requestLayout();
                }
            }

            binding.recyclerViewComentarios.smoothScrollToPosition(0);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsernameFromID(JSONObject post) {
        String url_selectUser = "http://212.227.40.235:3000/getUsernameAndImageFromID";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id_user", post.getString("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
        selectedUser.execute();
        String resultSearch = null;
        try {
            resultSearch = selectedUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resultSearch;
    }

    private boolean refreshViewPostInfo(String id) {
        boolean myLike = false;
        String url_selectPost = "http://212.227.40.235:3000/getSelectedPost/" + id;
        MyAsyncTaskGetSinglePost getSinglePost = new MyAsyncTaskGetSinglePost(url_selectPost);
        getSinglePost.execute();
        String resultSinglePost = null;
        try {
            resultSinglePost = getSinglePost.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Recargar los `likes`
        try {
            JSONObject new_dadesPost = new JSONObject(resultSinglePost);

            /*LIKES*/
            for (int i = 0; i < new_dadesPost.getJSONArray("likes").length() && !myLike; i++) {
                if (new_dadesPost.getJSONArray("likes").get(i).equals(dadesUsuari.getString("_id"))){
                    myLike = true;
                }
            }

            if (myLike){
                binding.likeImage.setImageResource(R.drawable.like);
            } else {
                binding.likeImage.setImageResource(R.drawable.no_like);
            }

            int new_numero_likes = new_dadesPost.getJSONArray("likes").length();
            if (new_numero_likes >= 10000 && new_numero_likes < 999950) {
                String likesString = formatLikes10(new_numero_likes);
                binding.numeroLikes.setText(likesString);
            } else if ((new_numero_likes) >= 999950){
                String likesString_100 = formatLikes100(new_numero_likes);
                binding.numeroLikes.setText(likesString_100);
            } else {
                binding.numeroLikes.setText(String.valueOf(new_numero_likes));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return myLike;
    }

    private void addNewLike(String id) {
        //Thread para dar like
        dadesUsuari = globalDadesUser.getDadesUser();
        isLiked = true;
        String url = "http://212.227.40.235:3000/newLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", id);
            body.put("id_user", dadesUsuari.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskLikes likesTask = new MyAsyncTaskLikes(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Establecer el numero de likes a +1 para ver el cambio
        int numero_likes = Integer.parseInt(binding.numeroLikes.getText().toString()) + 1;

        if (numero_likes >= 10000 && numero_likes < 999950) {
            String likesString = formatLikes10(numero_likes);
            binding.numeroLikes.setText(likesString);
        } else if ((numero_likes) >= 999950){
            String likesString_100 = formatLikes100(numero_likes);
            binding.numeroLikes.setText(likesString_100);
        } else {
            binding.numeroLikes.setText(String.valueOf(numero_likes));
        }
    }

    private void removeLike(String id) {
        //Quitar like
        dadesUsuari = globalDadesUser.getDadesUser();
        isLiked = false;
        String url = "http://212.227.40.235:3000/removeLike";
        JSONObject body = new JSONObject();
        try {
            body.put("id_post", id);
            body.put("id_user", dadesUsuari.getString("_id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        MyAsyncTaskLikes likesTask = new MyAsyncTaskLikes(url, body);
        likesTask.execute();
        String resultLikes = "";
        try {
            resultLikes = likesTask.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Establecer el numero de likes a como estaba antes del like
        int numero_likes = Integer.parseInt(binding.numeroLikes.getText().toString()) - 1;

        if (numero_likes >= 10000 && numero_likes < 999950) {
            String likesString = formatLikes10(numero_likes);
            binding.numeroLikes.setText(likesString);
        } else if ((numero_likes) >= 999950){
            String likesString_100 = formatLikes100(numero_likes);
            binding.numeroLikes.setText(likesString_100);
        } else {
            binding.numeroLikes.setText(String.valueOf(numero_likes));
        }
    }

    public static String formatLikes10(int num) {
        if (num >= 1000) {
            String numStr = String.valueOf((double) num / 1000);
            if (num % 1000 >= 100) {
                numStr = String.format("%.1f", (double) num / 1000);
            }
            return numStr + "k";
        } else {
            return String.valueOf(num);
        }
    }

    public static String formatLikes100(int num) {
        if (num >= 999950) {
            String numStr = String.valueOf((double) num / 1000000);
            if (num % 1000000 >= 100000) {
                numStr = String.format("%.1f", (double) num / 1000000);
            }
            return numStr + "M";
        } else {
            return String.valueOf(num);
        }
    }
}