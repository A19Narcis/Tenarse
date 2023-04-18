package com.example.tenarse.ui.newpost.videos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.fragment.app.Fragment;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentAddpostBinding;

public class FragmentAddVideos extends Fragment {
    private FragmentAddpostBinding binding;

    Button sendBtn;
    ImageView imageView;
    VideoView videoView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_add_videos, container, false);

        sendBtn = rootView.findViewById(R.id.uploadVideo);
        imageView = rootView.findViewById(R.id.preopen_video);
        videoView = rootView.findViewById(R.id.rv_post_video);
        videoView.setVisibility(View.GONE);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setVisibility(View.GONE);
                videoView.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }
}
