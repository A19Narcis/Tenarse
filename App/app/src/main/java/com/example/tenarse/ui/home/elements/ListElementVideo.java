package com.example.tenarse.ui.home.elements;

public class ListElementVideo {

    public String id;
    public String user_img_url;
    public String username;
    public String post_video_url;
    public String post_text;

    public ListElementVideo(String id, String username, String userImg, String videoURL, String textPost){
        this.id = id;
        this.username = username;
        this.user_img_url = userImg;
        this.post_video_url = videoURL;
        this.post_text = textPost;
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
}
