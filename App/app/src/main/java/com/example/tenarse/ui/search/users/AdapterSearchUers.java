package com.example.tenarse.ui.search.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class AdapterSearchUers extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataSearchList;
    private Context context;

    public AdapterSearchUers(List<Object> dataSearchList, Context context){
        this.dataSearchList = dataSearchList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_search_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListElementUser listElementUser = (ListElementUser) dataSearchList.get(position);
        UserViewHolder userViewHolder = (UserViewHolder) holder;

        String urlImagen = listElementUser.getUser_url_img().replace("localhost", "10.0.2.2.");
        Picasso.with(context).load(urlImagen).into(userViewHolder.userImg);

        userViewHolder.username.setText("@" + listElementUser.getSearch_username());

        userViewHolder.fullname.setText(listElementUser.getFullname());
    }

    @Override
    public int getItemCount() {
        return dataSearchList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        TextView username;
        TextView fullname;

        public UserViewHolder(View itemView){
            super(itemView);
            userImg = itemView.findViewById(R.id.rv_search_userImg);
            username = itemView.findViewById(R.id.rv_search_username);
            fullname = itemView.findViewById(R.id.rv_sarch_name);
        }
    }
}
