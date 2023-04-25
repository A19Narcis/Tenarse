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
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.newpost.httpUploads.MyAsyncTaskQuestion;
import com.example.tenarse.ui.register.MyAsyncTaskRegister;
import com.example.tenarse.ui.search.users.ListElementUser;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class FragmentAddQuestions extends Fragment {

    EditText title;
    EditText bodyQuestion;
    Button submitBtnQuestion;

    GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
    JSONObject dadesUsuari = globalDadesUser.getDadesUser();

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
                    body.put("tipus", "doubt");
                    body.put("title", title.getText().toString());
                    body.put("description", bodyQuestion.getText().toString());
                    body.put("hashtags", "[]");
                    body.put("imagen", "");
                    body.put("video", "");
                    body.put("owner", dadesUsuari.getString("username"));
                    body.put("user_img", dadesUsuari.getString("url_img"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                MyAsyncTaskQuestion addQuestionTask = new MyAsyncTaskQuestion(url_register, body);
                addQuestionTask.execute();
                String resultAddQuestion = null;
                try {
                    resultAddQuestion = addQuestionTask.get();
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (!resultAddQuestion.contains("false")){
                    Toast.makeText(getContext(), "¡Post subido!", Toast.LENGTH_SHORT).show();
                    title.setText("");
                    bodyQuestion.setText("");
                    //Update global dades
                    String url_selectUser = "http://10.0.2.2:3000/getSelectedUser";
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("username", dadesUsuari.getString("username"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MyAsyncTaskGetUser selectedUser = new MyAsyncTaskGetUser(url_selectUser, jsonBody);
                    selectedUser.execute();
                    String resultSearch = null;
                    try {
                        resultSearch = selectedUser.get();
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        JSONObject newDadesLogin = new JSONObject(resultSearch);
                        GlobalDadesUser globalDadesUser = GlobalDadesUser.getInstance();
                        globalDadesUser.setDadesUser(newDadesLogin);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


        return rootView;
    }
}
