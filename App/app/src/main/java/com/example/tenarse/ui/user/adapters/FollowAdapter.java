package com.example.tenarse.ui.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.profile.ProfileFragment;
import com.example.tenarse.ui.user.UserFragment;
import com.example.tenarse.ui.user.elements.ElementUserFollow;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> usersList;
    private UserFragment mUserFragment;
    private ProfileFragment mProfileFragment;
    private Context context;

    public FollowAdapter(List<Object> usersList, Context context, UserFragment userFragment) {
        this.usersList = usersList;
        this.context = context;
        this.mUserFragment = userFragment;
    }

    public FollowAdapter(List<Object> usersList, Context context, ProfileFragment profileFragment) {
        this.usersList = usersList;
        this.context = context;
        this.mProfileFragment = profileFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_follow, parent, false);
        return new FollowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ElementUserFollow userFollow = (ElementUserFollow) usersList.get(position);
        ViewHolder userViewHolder = (ViewHolder) holder;
        Picasso.with(context).load(userFollow.getUrl_image_user().replace("https://tenarse.online", "http://212.227.40.235")).into(userViewHolder.userImage);
        userViewHolder.userName.setText("@" + userFollow.getUsername());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName;

        public ViewHolder(View itemView){
            super(itemView);
            userImage = itemView.findViewById(R.id.userimg_follow);
            userName = itemView.findViewById(R.id.username_follow);
        }

    }
}
