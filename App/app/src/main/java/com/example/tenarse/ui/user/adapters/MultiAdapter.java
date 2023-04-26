package com.example.tenarse.ui.user.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.profile.ProfileFragment;
import com.example.tenarse.ui.user.UserFragment;
import com.example.tenarse.ui.user.elements.ListElementDoubt;
import com.example.tenarse.ui.user.elements.ListElementImg;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MultiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;
    private final int TYPE_IMAGE = 1;
    private final int TYPE_DOUBT = 2;
    private final int TYPE_VIDEO = 3;

    private UserFragment mUserFragment;
    private ProfileFragment mProfileFragment;
    private Context context;

    public MultiAdapter(List<Object> dataList, Context context, UserFragment mUserFragment) {
        this.dataList = dataList;
        this.mUserFragment = mUserFragment;
        this.context = context;
    }

    public MultiAdapter(List<Object> dataList, Context context, ProfileFragment mProfileFragment) {
        this.dataList = dataList;
        this.mProfileFragment = mProfileFragment;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof ListElementImg) {
            return TYPE_IMAGE;
        } else if (dataList.get(position) instanceof ListElementDoubt) {
            return TYPE_DOUBT;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_IMAGE:
                view = inflater.inflate(R.layout.list_element_user_img, parent, false);
                return new ImageViewHolder(view);
            case TYPE_DOUBT:
                view = inflater.inflate(R.layout.list_element_user_doubt, parent, false);
                return new MultiAdapter.DoubtViewHolder(view);
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
                Picasso.with(context).load(imgElement.getPost_img_url()).into(imageViewHolder.imageView);
                imageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUserFragment != null){
                            mUserFragment.selectPost(imgElement.getPost_img_id());
                        }

                        if (mProfileFragment != null) {
                            mProfileFragment.selectPost(imgElement.getPost_img_id());
                        }
                    }
                });
                break;
            case TYPE_DOUBT:
                ListElementDoubt doubtElement = (ListElementDoubt) dataList.get(position);
                DoubtViewHolder doubtViewHolder = (DoubtViewHolder) holder;

                doubtViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mUserFragment != null){
                            mUserFragment.selectPost(doubtElement.getDoubt_id());
                        }

                        if (mProfileFragment != null) {
                            mProfileFragment.selectPost(doubtElement.getDoubt_id());
                        }
                    }
                });

                doubtViewHolder.title.setText(doubtElement.getTitle());
                break;
        }
    }

    public void setList(List<Object> nuevaListaDatos) {
        this.dataList = nuevaListaDatos;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.userFeedPost);
        }
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.userFeedPost);
        }
    }
}
