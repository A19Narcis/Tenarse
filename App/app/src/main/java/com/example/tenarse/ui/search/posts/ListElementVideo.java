package com.example.tenarse.ui.search.posts;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ListElementVideo implements Serializable {
    public String user_img_url;
    public String username;
    public String post_video_url;
    public String post_video_text;
    public String post_video_id;

    public ListElementVideo(String username, String text, String post_url, String post_img_id, String user_img_url){
        this.username = username;
        this.post_video_text = text;
        this.post_video_url = post_url;
        this.post_video_id = post_img_id;
        this.user_img_url = user_img_url;
    }

    @NonNull
    @Override
    public String toString() {
        return username + ", " + post_video_text + ", " + post_video_url + ", " + post_video_id;
    }

    public String getPost_video_id() {
        return post_video_id;
    }

    public void setPost_video_id(String post_img_id) {
        this.post_video_id = post_img_id;
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

    public String getPost_video_text() {
        return post_video_text;
    }

    public void setPost_video_text(String post_img_text) {
        this.post_video_text = post_img_text;
    }
}
