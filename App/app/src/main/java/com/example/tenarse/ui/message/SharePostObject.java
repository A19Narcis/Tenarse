package com.example.tenarse.ui.message;

public class SharePostObject {
    String chat_profile_img;
    String chat_username;
    String id_post;
    String id_emisor;
    String id_chat;

    public SharePostObject(String chat_username, String chat_profile_img, String id_post, String id_emisor, String id_chat) {
        this.chat_profile_img = chat_profile_img;
        this.chat_username = chat_username;
        this.id_post = id_post;
        this.id_emisor = id_emisor;
        this.id_chat = id_chat;
    }

    public String getChat_profile_img() {
        return chat_profile_img;
    }

    public void setChat_profile_img(String chat_profile_img) {
        this.chat_profile_img = chat_profile_img;
    }

    public String getChat_username() {
        return chat_username;
    }

    public void setChat_username(String chat_username) {
        this.chat_username = chat_username;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId_emisor() {
        return id_emisor;
    }

    public void setId_emisor(String id_emisor) {
        this.id_emisor = id_emisor;
    }

    public String getId_chat() {
        return id_chat;
    }

    public void setId_chat(String id_chat) {
        this.id_chat = id_chat;
    }
}
