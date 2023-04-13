package com.example.tenarse.ui.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.home.elements.ListElementDoubt;
import com.example.tenarse.ui.home.elements.ListElementImg;

import java.util.List;

public class MultiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> dataList;
    private final int TYPE_IMAGE = 1;
    private final int TYPE_DOUBT = 2;
    private final int TYPE_VIDEO = 3;

    public MultiAdapter(List<Object> dataList) {
        this.dataList = dataList;
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
                view = inflater.inflate(R.layout.list_element_home_img, parent, false);
                return new ImageViewHolder(view);
            case TYPE_DOUBT:
                view = inflater.inflate(R.layout.list_element_home_doubt, parent, false);
                return new DoubtViewHolder(view);
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
                imageViewHolder.username.setText(imgElement.getUsername());
                break;
            case TYPE_DOUBT:
                ListElementDoubt doubtElement = (ListElementDoubt) dataList.get(position);
                DoubtViewHolder doubtViewHolder = (DoubtViewHolder) holder;
                doubtViewHolder.username.setText(doubtElement.getUsername());
                doubtViewHolder.title.setText(doubtElement.getTitle());
                doubtViewHolder.description.setText(doubtElement.getDescription());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView username;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.rv_post_image);
            username = itemView.findViewById(R.id.rv_username);
        }
    }

    public static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView title;
        TextView description;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.rv_username);
            title = itemView.findViewById(R.id.rv_title);
            description = itemView.findViewById(R.id.rv_description);
        }
    }
}