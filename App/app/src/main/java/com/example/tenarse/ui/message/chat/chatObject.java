package com.example.tenarse.ui.message.chat;

import android.graphics.Bitmap;

public class chatObject {
    String userName;
    String lastMsg;
    Bitmap profileImg;

    public chatObject(String userName, String lastMsg, Bitmap profileImg) {
        this.userName = userName;
        this.lastMsg = lastMsg;
        this.profileImg = profileImg;
    }

    public chatObject(String userName, String lastMsg) {
        this.userName = userName;
        this.lastMsg = lastMsg;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Bitmap getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(Bitmap profileImg) {
        this.profileImg = profileImg;
    }
}
