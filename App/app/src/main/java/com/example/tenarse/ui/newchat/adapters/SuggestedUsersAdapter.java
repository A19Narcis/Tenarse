package com.example.tenarse.ui.newchat.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.ui.newchat.SuggestedUsersObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SuggestedUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<SuggestedUsersObject> dataList;
    private Context context;

    public SuggestedUsersAdapter(ArrayList<SuggestedUsersObject> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_element_chat_suggested_user, parent, false);
        return new SuggestedUsersAdapter.usersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SuggestedUsersAdapter.usersViewHolder uvh = (SuggestedUsersAdapter.usersViewHolder) holder;
        uvh.userName.setText("@" + dataList.get(position).getUserName());
        Picasso.with(context).load(dataList.get(position).getPorfileImg().replace("https://tenarse.online", "http://212.227.40.235")).into(uvh.profileImg);
        uvh.constraintLayout.setOnClickListener(view ->{
            if (uvh.selectBtn.isChecked()){
                uvh.selectBtn.setChecked(false);
                dataList.get(position).setSelected(false);
            }else {
                uvh.selectBtn.setChecked(true);
                dataList.get(position).setSelected(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder {
        TextView userName;

        ImageView profileImg;

        RadioButton selectBtn;

        ConstraintLayout constraintLayout;


        public usersViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            profileImg = itemView.findViewById(R.id.profileImg);
            selectBtn = itemView.findViewById(R.id.selectBtn);
            constraintLayout = itemView.findViewById(R.id.element_suggested_user);
        }
    }
}
