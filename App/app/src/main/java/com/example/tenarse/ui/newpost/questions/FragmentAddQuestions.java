package com.example.tenarse.ui.newpost.questions;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.globals.MyAsyncTask;
import com.example.tenarse.ui.newpost.NewpostFragment;
import com.example.tenarse.ui.newpost.adapters.HashtagAdapter;
import com.example.tenarse.ui.newpost.httpUploads.MyAsyncTaskQuestion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FragmentAddQuestions extends Fragment {

    EditText title;
    EditText bodyQuestion;
    Button submitBtnQuestion;

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapter;
    RecyclerView recyclerView;
    HashtagAdapter hashtagAdapter;
    TextView errorText;
    TextView errorFaltanCampos;
    ArrayList<String> arrayRecycler = new ArrayList<>();

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

        autoCompleteTextView = rootView.findViewById(R.id.autoCompleteDoubt);
        adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.opciones_autocompletado));
        autoCompleteTextView.setAdapter(adapter);
        errorText = rootView.findViewById(R.id.errorText);
        errorFaltanCampos = rootView.findViewById(R.id.errorFaltanCampos);


        hashtagAdapter = new HashtagAdapter(arrayRecycler, adapter, getContext());
        recyclerView = rootView.findViewById(R.id.add_recyclerView_doubt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(hashtagAdapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = (String) parent.getItemAtPosition(position);
                arrayRecycler.add(seleccion);
                hashtagAdapter.notifyItemInserted(arrayRecycler.size() - 1);
                adapter.remove(seleccion);
                adapter.notifyDataSetChanged();
                autoCompleteTextView.setText("");
                autoCompleteTextView.setHint(autoCompleteTextView.getHint());
                // Cierra la lista de autocompletado
                autoCompleteTextView.dismissDropDown();
                if (errorText.getVisibility() == View.VISIBLE){
                    errorText.setVisibility(View.GONE);
                }
            }
        });

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                int actionID = 6;
                actionId = actionID;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String userInput = autoCompleteTextView.getText().toString();
                    if (userInput.length() > 0 && userInput.startsWith("#")){
                        if (errorText.getVisibility() == View.VISIBLE){
                            errorText.setVisibility(View.GONE);
                        }
                        arrayRecycler.add(userInput);
                        hashtagAdapter.notifyItemInserted(arrayRecycler.size() - 1);
                        autoCompleteTextView.setText("");
                    } else if (!userInput.startsWith("#")) {
                        errorText.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
                return false;
            }
        });

        String url_register = "http://212.227.40.235:3000/addPostDubt";

        submitBtnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (title.getText().toString().length() == 0 || bodyQuestion.getText().toString().length() == 0){
                    errorFaltanCampos.setVisibility(View.VISIBLE);
                } else {
                    if (errorFaltanCampos.getVisibility() == View.VISIBLE){
                        errorFaltanCampos.setVisibility(View.GONE);
                    }
                    JSONObject body = new JSONObject();
                    JSONArray comments = new JSONArray();
                    JSONArray hashtags = new JSONArray(arrayRecycler);
                    try {
                        body.put("type", "doubt");
                        body.put("title", title.getText().toString());
                        body.put("text", bodyQuestion.getText().toString());
                        body.put("comments", comments);
                        body.put("owner", dadesUsuari.getString("_id"));
                        body.put("hashtags", hashtags);
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

                        /* RECARGAR FRAGMENT */
                        NewpostFragment newpostFragment = (NewpostFragment) getParentFragment();
                        newpostFragment.postUploaded();

                        //Update global dades
                        String url_selectUser = "http://212.227.40.235:3000/getSelectedUser";
                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("username", dadesUsuari.getString("username"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MyAsyncTask selectedUser = new MyAsyncTask(url_selectUser, jsonBody);
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
            }
        });


        return rootView;
    }
}
