package com.example.tenarse.ui.newpost.questions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tenarse.MainActivity;
import com.example.tenarse.R;
import com.example.tenarse.ui.newpost.httpUploads.MyAsyncTaskQuestion;
import com.example.tenarse.ui.register.MyAsyncTaskRegister;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class FragmentAddQuestions extends Fragment {

    EditText title;
    EditText bodyQuestion;
    Button submitBtnQuestion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_add_questions, container, false);
        title = rootView.findViewById(R.id.editTextTitleQuestion);
        bodyQuestion = rootView.findViewById(R.id.editTextQuestionBody);
        submitBtnQuestion = rootView.findViewById(R.id.submitBtnQuestion);

        String url_register = "http://10.0.2.2:3000/addNewPost";

        submitBtnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject body = new JSONObject();
                try {
                    body.put("title", title.getText().toString());
                    body.put("description", bodyQuestion.getText().toString());
                    body.put("hashtags", "{}");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MyAsyncTaskQuestion registerUser = new MyAsyncTaskQuestion(url_register, body);
                registerUser.execute();
                String resultRegister = null;
                try {
                    resultRegister = registerUser.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!resultRegister.contains("false")){
                    Toast.makeText(getContext(), "Subido!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return rootView;
    }
}
