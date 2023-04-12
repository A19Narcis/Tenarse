package com.example.tenarse.ui.home.adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.tenarse.R;
import com.example.tenarse.ui.home.elements.ListElementImg;

import java.util.List;

public class ListElementImgAdapter extends RecyclerView.Adapter<ListElementImgAdapter.ViewHolder> {
    private List<ListElementImg> listElementImgs;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListElementImgAdapter(List<ListElementImg> itemList, Context context){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.listElementImgs = itemList;
    }

    @Override
    public int getItemCount() {
        return listElementImgs.size();
    }


    @Override
    public ListElementImgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_home_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListElementImgAdapter.ViewHolder holder, int position) {
        holder.bindData(listElementImgs.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Items que tiene cada elemento de la RecyclerView
        public ImageView user_image;
        public TextView username;
        public ImageView post_image;

        public ViewHolder(View itemView){
            super(itemView);
            user_image = itemView.findViewById(R.id.rv_userImage);
            username = itemView.findViewById(R.id.rv_username);
            post_image = itemView.findViewById(R.id.rv_post_image);
        }

        public void bindData(ListElementImg listElementImg){
            /* Modificar las imagenes de perfil / post i tambien el texto de username */
            username.setText(listElementImg.getUsername());
        }
    }


}
