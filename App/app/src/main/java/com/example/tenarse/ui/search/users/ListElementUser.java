package com.example.tenarse.ui.search.users;

public class ListElementUser {
    public String user_url_img;
    public String search_username;
    public String fullname;

    public ListElementUser(String user_url_img, String username, String fullname) {
        this.user_url_img = user_url_img;
        this.search_username = username;
        this.fullname = fullname;
    }

    public String getUser_url_img() {
        return user_url_img;
    }

    public void setUser_url_img(String user_url_img) {
        this.user_url_img = user_url_img;
    }

    public String getSearch_username() {
        return search_username;
    }

    public void setSearch_username(String search_username) {
        this.search_username = search_username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
