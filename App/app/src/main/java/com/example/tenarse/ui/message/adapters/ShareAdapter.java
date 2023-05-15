package com.example.tenarse.ui.message.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.message.SharePostObject;
import com.example.tenarse.ui.profile.ProfileFragment;
import com.example.tenarse.ui.user.UserFragment;
import com.example.tenarse.ui.user.elements.ElementUserFollow;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SharePostObject> usersList;
    private Context context;

    public ShareAdapter(List<SharePostObject> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_follow, parent, false);
        return new ShareAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SharePostObject userFollow = usersList.get(position);
        ViewHolder userViewHolder = (ViewHolder) holder;
        Picasso.with(context).load(userFollow.getProfile_img().replace("localhost", "10.0.2.2")).into(userViewHolder.userImage);
        userViewHolder.userName.setText("@" + userFollow.getUsername());
        userViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;

        ConstraintLayout constraintLayout;

        public ViewHolder(View itemView){
            super(itemView);
            userImage = itemView.findViewById(R.id.userimg_follow);
            userName = itemView.findViewById(R.id.username_follow);
            constraintLayout = itemView.findViewById(R.id.search_user);
        }

    }
}
