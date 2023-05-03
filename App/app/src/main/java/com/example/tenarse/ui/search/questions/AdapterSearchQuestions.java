package com.example.tenarse.ui.search.questions;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.home.HomeViewModel;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterSearchQuestions extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;
    private SearchQuestionsFragment mSearchQuestionsFragment;
    private Context context;

    public AdapterSearchQuestions(List<Object> dataList, Context context, SearchQuestionsFragment mSearchQuestionsFragment) {
        this.dataList = dataList;
        this.mSearchQuestionsFragment = mSearchQuestionsFragment;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_home_doubt_search, parent, false);
        return new AdapterSearchQuestions.DoubtViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListElementDoubt doubtElement = (ListElementDoubt) dataList.get(position);
        DoubtViewHolder doubtViewHolder = (DoubtViewHolder) holder;
        doubtViewHolder.username.setText(doubtElement.getUsername());

        doubtViewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Aquí
                mSearchQuestionsFragment.selectUser(doubtViewHolder.username.getText().toString(), v);
            }
        });

        doubtViewHolder.title.setText(doubtElement.getTitle());
        doubtViewHolder.description.setText(doubtElement.getDescription());

        /* Cargar USER IMAGE BITMAT Hilo */
        String urlUserDoubt = doubtElement.getUser_img_url().replace("localhost", "10.0.2.2");
        Picasso.with(context).load(urlUserDoubt).into(doubtViewHolder.userImageView);

        doubtViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchQuestionsFragment.selectPost(doubtElement.getId(), v, doubtElement.getUsername());
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
                    mSearchQuestionsFragment.addLike(doubtElement.getId());
                    doubtViewHolder.likeImage.setImageResource(R.drawable.like);
                    doubtElement.setLiked(true);
                } else if (doubtElement.isLiked()) {
                    mSearchQuestionsFragment.removeLike(doubtElement.getId());
                    doubtViewHolder.likeImage.setImageResource(R.drawable.no_like);
                    doubtElement.setLiked(false);
                }
            }
        });
    }


    public void setList(List<Object> nuevaListaDatos) {
        this.dataList = nuevaListaDatos;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;
        ImageView userImageView;
        ImageView likeImage;


        public DoubtViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            title = itemView.findViewById(R.id.rv_title);
            description = itemView.findViewById(R.id.rv_description);
            userImageView = itemView.findViewById(R.id.rv_userImage);
            likeImage = itemView.findViewById(R.id.like_image);
        }
    }

}
