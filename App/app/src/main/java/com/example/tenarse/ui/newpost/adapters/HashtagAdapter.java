package com.example.tenarse.ui.newpost.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;

import java.util.ArrayList;

public class HashtagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> dataList;
    private Context context;

    private ArrayAdapter<String> autoCompleteadapter;


    public HashtagAdapter(ArrayList<String> dataList, ArrayAdapter<String> autoCompleteadapter, Context context) {
        this.dataList = dataList;
        this.autoCompleteadapter = autoCompleteadapter;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_add_hashtags, parent, false);
        return new HastagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HastagViewHolder hvh = (HastagViewHolder) holder;
        hvh.textoHashtag.setText(dataList.get(position));
        hvh.hashtagCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = ((HastagViewHolder) holder).textoHashtag.getText().toString();
                autoCompleteadapter.add(texto);
                autoCompleteadapter.notifyDataSetChanged();
                dataList.remove(holder.getBindingAdapterPosition());
                notifyItemRemoved(holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class HastagViewHolder extends RecyclerView.ViewHolder {
        TextView textoHashtag;
        CardView hashtagCardView;

        public HastagViewHolder(View itemView) {
            super(itemView);
            textoHashtag = itemView.findViewById(R.id.hashtagTextView);
            hashtagCardView = itemView.findViewById(R.id.hashtagCardView);
        }
    }
}