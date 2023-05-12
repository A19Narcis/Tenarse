package com.example.tenarse.ui.user.elements;

public class ElementUserFollow {
    private String username;
    private String url_image_user;

    public ElementUserFollow(String username, String url_image_user) {
        this.username = username;
        this.url_image_user = url_image_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl_image_user() {
        return url_image_user;
    }

    public void setUrl_image_user(String url_image_user) {
        this.url_image_user = url_image_user;
    }
}

