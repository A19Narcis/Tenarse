package com.example.tenarse.ui.home.elements;

import org.json.JSONArray;

public class ListElementImg {

    public String id;
    public String user_img_url;
    public String username;
    public String post_img_url;
    public String post_img_text;

    public JSONArray likes;
    public boolean isLiked;

    public ListElementImg(String id, String username, String text, String urlImg, String userImg, JSONArray likes){
        this.id = id;
        this.username = username;
        this.post_img_text = text;
        this.post_img_url = urlImg;
        this.user_img_url = userImg;
        this.likes = likes;
    }

    public JSONArray getLikes() {
        return likes;
    }

    public void setLikes(JSONArray likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
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

    public String getPost_img_url() {
        return post_img_url;
    }

    public void setPost_img_url(String post_img_url) {
        this.post_img_url = post_img_url;
    }

    public String getPost_img_text() {
        return post_img_text;
    }

    public void setPost_img_text(String post_img_text) {
        this.post_img_text = post_img_text;
    }
}
