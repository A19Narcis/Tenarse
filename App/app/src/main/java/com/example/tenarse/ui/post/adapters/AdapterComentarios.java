package com.example.tenarse.ui.post.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.post.elements.Comentario;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterComentarios extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Comentario> comentarioList;
    private Context context;

    public AdapterComentarios(List<Comentario> comentarioList, Context context) {
        this.comentarioList = comentarioList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comentario comentario = comentarioList.get(position);
        ComentarioViewHolder comentarioViewHolder = (ComentarioViewHolder) holder;

        String userImagenLocal = comentario.getCom_user_img();
        Picasso.with(context).load(userImagenLocal).into(comentarioViewHolder.userImage);

        comentarioViewHolder.userName.setText(comentario.getCom_username());
        comentarioViewHolder.comentario.setText(comentario.getCom_text());
    }

    @Override
    public int getItemCount() {
        return comentarioList.size();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        TextView comentario;

        public ComentarioViewHolder(View itemView){
            super(itemView);
            userImage = itemView.findViewById(R.id.com_userImage);
            userName = itemView.findViewById(R.id.com_username);
            comentario = itemView.findViewById(R.id.com_text);
        }
    }
}
