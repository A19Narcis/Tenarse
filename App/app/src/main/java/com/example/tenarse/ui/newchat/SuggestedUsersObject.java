package com.example.tenarse.ui.newchat;

import android.net.Uri;

public class SuggestedUsersObject {
    String porfileImg;
    String userName;
    String id;
    boolean selected;

    public SuggestedUsersObject(String porfileImg, String userName, String id) {
        this.porfileImg = porfileImg;
        this.userName = userName;
        this.id = id;
        this.selected = false;
    }

    public String getPorfileImg() {
        return porfileImg;
    }

    public void setPorfileImg(String porfileImg) {
        this.porfileImg = porfileImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
