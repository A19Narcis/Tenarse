package com.example.tenarse.ui.active_chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tenarse.R;
import com.example.tenarse.databinding.FragmentActiveChatBinding;
import com.example.tenarse.databinding.FragmentNotificacionesBinding;
import com.example.tenarse.globals.GlobalDadesUser;
import com.example.tenarse.httpRetrofit.ApiService;
import com.example.tenarse.ui.active_chat.adapters.ActiveChatMultiAdapter;
import com.example.tenarse.ui.home.asynctask.MyAsyncTaskGetUser;
import com.example.tenarse.ui.message.chat.chatObject;
import com.example.tenarse.ui.register.MyAsyncTaskRegister;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class activeChat extends Fragment {

    private FragmentActiveChatBinding binding;
    RecyclerView recyclerView;
    ArrayList<Object> arrayRecycler = new ArrayList<>();
    ActiveChatMultiAdapter chatAdapter;

    GlobalDadesUser globalDadesUser;
    JSONObject dadesUsuari;

    String chat_id;

    String username;

    String profile_img;

    ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentActiveChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.backToMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
            }
        });


        Bundle args = getArguments();
        if (args != null){
            chat_id = args.getString("chat_id");
            username = args.getString("username");
            profile_img = args.getString("profile_img");
        }

        /*arrayRecycler.add(new MessageObject("1","sergi", "Hola que tal?"));
        arrayRecycler.add(new MessageObject("2","teo", "Toi bien"));
        arrayRecycler.add(new PostObject("2","http://localhost:3000/uploads\\user_img\\6459f1fb6e52fac56af41ad7.png", "http://localhost:3000/uploads\\user_img\\6459f1fb6e52fac56af41ad7.png", "@narcis", ""));
*/
        chatAdapter = new ActiveChatMultiAdapter(arrayRecycler, getContext(), new activeChat());
        recyclerView = binding.rvChat;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);

        binding.sendBtn.setOnClickListener(view -> {
            globalDadesUser = GlobalDadesUser.getInstance();
            dadesUsuari = globalDadesUser.getDadesUser();
            JSONObject body = new JSONObject();

            try {
                body.put("chat_id", chat_id);
                System.out.println("DADES USUARI EN EL JSOOOON: " + dadesUsuari.toString());
                body.put("emisor", dadesUsuari.getString("_id"));
                body.put("message", binding.msgTextView.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url_updateDades = "http://10.0.2.2:3000/newMessage";
            MyAsyncTaskGetUser updateUser = new MyAsyncTaskGetUser(url_updateDades, body);
            updateUser.execute();
            String resultUpdate = null;
            try {
                resultUpdate = updateUser.get();
                System.out.println(resultUpdate);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                System.out.println("DATOS RECYCLEEEER: "+dadesUsuari.toString());
                arrayRecycler.add(new MessageObject(dadesUsuari.getString("_id"), dadesUsuari.getString("username"), binding.msgTextView.getText().toString()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            chatAdapter.notifyItemInserted(arrayRecycler.size()-1);
            binding.msgTextView.setText("");
        });
        return root;
    }

    private void initRetrofitClient(){
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000").client(client).build().create(ApiService.class);
    }

}
