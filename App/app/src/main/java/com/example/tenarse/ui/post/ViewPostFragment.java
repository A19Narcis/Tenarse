package com.example.tenarse.ui.post;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tenarse.Login;
import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentProfileBinding;
import com.example.tenarse.databinding.FragmentViewPostBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.HomeFragment;
import com.example.tenarse.ui.post.adapters.AdapterComentarios;
import com.example.tenarse.ui.post.asynctask.MyAsyncTaskComment;
import com.example.tenarse.ui.post.asynctask.MyAsyncTaskDeletePost;
import com.example.tenarse.ui.post.elements.Comentario;
import com.example.tenarse.ui.search.SearchFragment;
import com.example.tenarse.ui.user.UserFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kotlin.jvm.internal.PropertyReference0Impl;

public class ViewPostFragment extends Fragment {

    private FragmentViewPostBinding binding;
    private String infoPost;

    private List<Comentario> comentarioList;
    private AdapterComentarios adapterComentarios;
    private int numeroComentarios = 0;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

    String fragmentAnterior = "";

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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        String originFragment = "";
        Bundle args = getArguments();
        if (args != null){
            infoPost = args.getString("infoPost");
            fragmentAnterior = args.getString("fragment");
            originFragment = args.getString("origin");
        }

        try {
            JSONObject dadesPost = new JSONObject(infoPost);
            binding.rvUsername.setText(dadesPost.getString("owner"));
            String userImg = dadesPost.getString("user_img").replace("localhost", "10.0.2.2");
            Picasso.with(getContext()).load(userImg).into(binding.rvUserImage);

            //Veure l'icone de borrar 'post' si es teu el post
            if (!dadesUsuari.getString("username").equals(dadesPost.getString("owner")) || !originFragment.equals("perfil")){
                binding.removeButton.setVisibility(View.GONE);
            } else {
                binding.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Esborrar post
                        System.out.println("CLIIIIIICK");
                        AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(getActivity());
                        alertaLogOut.setTitle("Eliminar publicación");
                        alertaLogOut.setMessage("¿Quieres eliminar esta publicación?");
                        alertaLogOut.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String url = "http://10.0.2.2:3000/deletePost";
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

                                getActivity().getSupportFragmentManager().popBackStack();
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
                binding.rvTitle.setText(dadesPost.getString("titol"));
                binding.rvPostText.setText(dadesPost.getString("text"));
            } else if (dadesPost.getString("tipus").equals("image")){
                binding.rvTitle.setVisibility(View.GONE);
                Picasso.with(getContext()).load(dadesPost.getString("url_img")).into(binding.rvPostImage);
            } else if (dadesPost.getString("tipus").equals("video")){
                binding.rvTitle.setVisibility(View.GONE);
            }

            int numero_likes = dadesPost.getJSONArray("likes").length();
            System.out.println("NUMERO LIKES: " + numero_likes);
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


            //Cargar los comentarios
            for (int i = 0; i < dadesPost.getJSONArray("comentaris").length(); i++) {
                JSONObject comentarioNuevo = (JSONObject) dadesPost.getJSONArray("comentaris").get(i);
                comentarioList.add(0, new Comentario(comentarioNuevo.getString("user_img"), comentarioNuevo.getString("user"), comentarioNuevo.getString("coment_text")));
                adapterComentarios.notifyItemInserted(0);
            }


            binding.subirComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((binding.editTextComentario.getText().length() > 0 && binding.editTextComentario.getText().length() <= 100) || (binding.editTextComentario.getText().toString().replace(" ", "").length() > 0 && binding.editTextComentario.getText().toString().replace(" ", "").length() <= 100)){
                        String urlUploadComment = "http://10.0.2.2:3000/addNewComment";
                        JSONObject commentBody = new JSONObject();
                        JSONObject innerComentari = new JSONObject();
                        try {
                            innerComentari.put("user_img", dadesUsuari.getString("url_img"));
                            innerComentari.put("user", dadesUsuari.getString("username"));
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
                            comentarioList.add(0, new Comentario(innerComentari.getString("user_img"), innerComentari.getString("user"), innerComentari.getString("coment_text")));
                            adapterComentarios.notifyItemInserted(0);
                            binding.recyclerViewComentarios.smoothScrollToPosition(0);
                            numeroComentarios++;
                            binding.numeroComentarios.setText("Comentarios (" + numeroComentarios + ")");
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


        binding.editTextComentario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.textNumChars.setText(s.length() + "/100");
                if (s.length() > 100){
                    binding.textNumChars.setTextColor(Color.RED);
                } else if (s.length() <= 100){
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

        return root;
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