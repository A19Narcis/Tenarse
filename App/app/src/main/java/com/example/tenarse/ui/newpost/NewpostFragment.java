package com.example.tenarse.ui.newpost;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentAddpostBinding;
import com.example.tenarse.ui.newpost.images.FragmentAddImages;
import com.example.tenarse.ui.newpost.questions.FragmentAddQuestions;
import com.example.tenarse.ui.newpost.videos.FragmentAddVideos;

public class NewpostFragment extends Fragment {

    private FragmentAddpostBinding binding;

    ImageView imageBtn;
    ImageView videoBtn;
    ImageView questionBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        NewpostViewModel newpostViewModel =
                new ViewModelProvider(this).get(NewpostViewModel.class);

        binding = FragmentAddpostBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        FragmentAddImages FragmentAddImages = new FragmentAddImages();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, FragmentAddImages);
        transaction.commit();

        imageBtn = binding.imageBtn;
        videoBtn = binding.videoBtn;
        questionBtn = binding.questionBtn;

        binding.cancelarPostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = getResources().getDrawable(R.drawable.selected_images);
                imageBtn.setImageDrawable(drawable);
                drawable = getResources().getDrawable(R.drawable.unsel_videos);
                videoBtn.setImageDrawable(drawable);
                drawable = getResources().getDrawable(R.drawable.unsel_questions);
                questionBtn.setImageDrawable(drawable);
                FragmentAddImages fragmentAddVideos = new FragmentAddImages();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentAddVideos);
                transaction.commit();
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = getResources().getDrawable(R.drawable.unsel_images);
                imageBtn.setImageDrawable(drawable);
                drawable = getResources().getDrawable(R.drawable.selected_videos);
                videoBtn.setImageDrawable(drawable);
                drawable = getResources().getDrawable(R.drawable.unsel_questions);
                questionBtn.setImageDrawable(drawable);
                FragmentAddVideos fragmentAddVideos = new FragmentAddVideos();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentAddVideos);
                transaction.commit();
            }
        });

       questionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = getResources().getDrawable(R.drawable.unsel_images);
                imageBtn.setImageDrawable(drawable);
                drawable = getResources().getDrawable(R.drawable.unsel_videos);
                videoBtn.setImageDrawable(drawable);
                drawable = getResources().getDrawable(R.drawable.selected_questions);
                questionBtn.setImageDrawable(drawable);
                FragmentAddQuestions fragmentAddQuestions = new FragmentAddQuestions();
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragmentAddQuestions);
                transaction.commit();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}