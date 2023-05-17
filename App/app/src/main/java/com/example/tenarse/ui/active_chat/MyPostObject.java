package com.example.tenarse.ui.active_chat;

public class MyPostObject {
    String emisor_id;
    String emisor_username;
    String id_post;

    String username;
    String post_image;
    String owner_post_image;
    String post_text;

    public MyPostObject(String emisor_id, String emisor_username, String id_post, String post_image, String owner_post_image, String post_text, String username) {
        this.emisor_id = emisor_id;
        this.emisor_username = emisor_username;
        this.id_post = id_post;
        this.post_image = post_image;
        this.owner_post_image = owner_post_image;
        this.post_text = post_text;
        this.username = username;
    }

    public String getEmisor_id() {
        return emisor_id;
    }

    public void setEmisor_id(String emisor_id) {
        this.emisor_id = emisor_id;
    }

    public String getEmisor_username() {
        return emisor_username;
    }

    public void setEmisor_username(String emisor_username) {
        this.emisor_username = emisor_username;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }

    public String getOwner_post_image() {
        return owner_post_image;
    }

    public void setOwner_post_image(String owner_post_image) {
        this.owner_post_image = owner_post_image;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}



