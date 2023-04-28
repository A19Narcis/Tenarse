package com.example.tenarse.ui.home.elements;

import org.json.JSONArray;

public class ListElementVideo {

    public String id;
    public String user_img_url;
    public String username;
    public String post_video_url;
    public String post_text;

    public JSONArray likes;
    public boolean isLiked;

    public ListElementVideo(String id, String username, String userImg, String videoURL, String textPost, JSONArray likes){
        this.id = id;
        this.username = username;
        this.user_img_url = userImg;
        this.post_video_url = videoURL;
        this.post_text = textPost;
        this.likes = likes;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
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

    public String getPost_video_url() {
        return post_video_url;
    }

    public void setPost_video_url(String post_img_url) {
        this.post_video_url = post_img_url;
    }

    public JSONArray getLikes() {
        return likes;
    }

    public void setLikes(JSONArray likes) {
        this.likes = likes;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    public boolean isLiked() {
        return isLiked;
    }
}
