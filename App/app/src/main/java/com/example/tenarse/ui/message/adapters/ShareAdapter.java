package com.example.tenarse.ui.message.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.message.SharePostObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.socket.client.Socket;

public class ShareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private MainActivity mainActivity;
    private List<SharePostObject> usersList;
    private Context context;

    private Dialog dialog;

    public ShareAdapter(List<SharePostObject> usersList, Context context, MainActivity mainActivity, Dialog dialog) {
        this.usersList = usersList;
        this.context = context;
        this.mainActivity = mainActivity;
        this.dialog = dialog;
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
        Picasso.with(context).load(userFollow.getChat_profile_img().replace("localhost", "10.0.2.2")).into(userViewHolder.userImage);
        userViewHolder.userName.setText("@" + userFollow.getChat_username());
        userViewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPost(userFollow);
                dialog.dismiss();
            }
        });
    }

    private void sendPost(SharePostObject userFollow) {
        JSONObject body = new JSONObject();
        try {
            body.put("chat_id", userFollow.getId_chat());
            body.put("emisor", userFollow.getId_emisor());
            body.put("post_id", userFollow.getId_post());
            body.put("message", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Socket mSocket = mainActivity.getmSocket();
        mSocket.emit("sendMessage", body);
        String url_updateDades = "http://10.0.2.2:3000/newMessage";
        MyAsyncTask updateUser = new MyAsyncTask(url_updateDades, body);
        updateUser.execute();
        String resultUpdate = null;
        try {
            resultUpdate = updateUser.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
