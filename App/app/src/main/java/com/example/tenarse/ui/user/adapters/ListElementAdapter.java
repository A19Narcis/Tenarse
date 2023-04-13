package com.example.tenarse.ui.user.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.user.elements.ListElementImg;

import java.util.List;

public class ListElementAdapter extends RecyclerView.Adapter<ListElementAdapter.ViewHolder> {
    private List<ListElementImg> listElementImgs;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListElementAdapter(List<ListElementImg> itemList, Context context){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.listElementImgs = itemList;
    }

    @Override
    public int getItemCount() {
        return listElementImgs.size();
    }


    @Override
    public ListElementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_element_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListElementAdapter.ViewHolder holder, int position) {
        holder.bindData(listElementImgs.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Items que tiene cada elemento de la RecyclerView
        public ImageView post_image;

        public ViewHolder(View itemView){
            super(itemView);
            post_image = itemView.findViewById(R.id.userFeedPost);
        }

        public void bindData(ListElementImg listElementImg){
            /* Modificar las imagenes de perfil / post i tambien el texto de username */
        }
    }


}
