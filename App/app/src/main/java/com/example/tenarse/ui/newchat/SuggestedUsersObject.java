package com.example.tenarse.ui.newchat;

import android.net.Uri;

public class SuggestedUsersObject {
    String porfileImg;
    String userName;

    public SuggestedUsersObject(String porfileImg, String userName) {
        this.porfileImg = porfileImg;
        this.userName = userName;
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
}
