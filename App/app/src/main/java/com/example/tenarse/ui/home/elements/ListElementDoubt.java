package com.example.tenarse.ui.home.elements;

import org.json.JSONArray;

public class ListElementDoubt {

    public String id;
    public String user_img_url;
    public String username;
    public String title;
    public String description;
    public String id_owner;
    public JSONArray likes;
    public boolean isLiked;

    public ListElementDoubt(String id, String username, String title, String descr, String userImg, JSONArray likes, String id_owner){
        this.id = id;
        this.username = username;
        this.title = title;
        this.description = descr;
        this.user_img_url = userImg;
        this.likes = likes;
        this.id_owner = id_owner;
    }

    public String getId_owner() {
        return id_owner;
    }

    public void setId_owner(String id_owner) {
        this.id_owner = id_owner;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public JSONArray getLikes() {
        return likes;
    }

    public void setLikes(JSONArray likes) {
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_img_url() {
        return user_img_url;
    }

    public void setUser_img_url(String user_img_url) {
        this.user_img_url = user_img_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

