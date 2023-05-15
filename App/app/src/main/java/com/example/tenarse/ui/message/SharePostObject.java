package com.example.tenarse.ui.message;

public class SharePostObject {
    String id_post;

    String postOwner_username;

    String postOwner_profile_img;

    String profile_img;
    String username;

    public SharePostObject(String username, String profile_img, String id_post, String postOwner_profile_img, String postOwner_username) {
        this.id_post = id_post;
        this.profile_img = profile_img;
        this.postOwner_username = postOwner_username;
        this.postOwner_profile_img = postOwner_profile_img;
        this.username = username;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getProfile_img() {
        return postOwner_profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.postOwner_profile_img = profile_img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
