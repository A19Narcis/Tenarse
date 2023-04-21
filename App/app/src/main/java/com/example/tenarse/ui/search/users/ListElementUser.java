package com.example.tenarse.ui.search.users;

import org.json.JSONArray;

import java.io.Serializable;

public class ListElementUser implements Serializable {
    public String user_url_img;
    public String search_username;
    public String fullname;
    public int followers_search;
    public int following_search;
    public JSONArray publicacions_search;

    public ListElementUser(String user_url_img, String username, String fullname, int followers_search, int following_search, JSONArray posts) {
        this.user_url_img = user_url_img;
        this.search_username = username;
        this.fullname = fullname;
        this.followers_search = followers_search;
        this.following_search = following_search;
        this.publicacions_search = posts;
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

    public int getFollowers_search() {
        return followers_search;
    }

    public void setFollowers_search(int followers_search) {
        this.followers_search = followers_search;
    }

    public int getFollowing_search() {
        return following_search;
    }

    public void setFollowing_search(int following_search) {
        this.following_search = following_search;
    }

    public JSONArray getPublicacions_search() {
        return publicacions_search;
    }

    public void setPublicacions_search(JSONArray publicacions_search) {
        this.publicacions_search = publicacions_search;
    }

    @Override
    public String toString(){
        return "Username: " + this.search_username + " IMG: " + this.user_url_img + " Name: " + this.fullname + " Followers: " + this.followers_search + " Following: " + this.following_search + " Posts: " + this.publicacions_search;
    }
}
