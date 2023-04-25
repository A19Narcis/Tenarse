package com.example.tenarse.ui.message.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tenarse.R;
import com.example.tenarse.ui.message.adapters.ChatAdapter;
import com.example.tenarse.ui.newpost.adapters.HashtagAdapter;

import java.util.ArrayList;

public class FragmentChat extends Fragment {

    RecyclerView recyclerView;

    ArrayList<chatObject> arrayRecycler = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        arrayRecycler.add(new chatObject("SergiMS03", "Hola como te encuentras hoy?"));
        ChatAdapter chatAdapter = new ChatAdapter(arrayRecycler, getContext());
        recyclerView = rootView.findViewById(R.id.rv_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(chatAdapter);
        return rootView;
    }
}