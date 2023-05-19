package com.example.tenarse.ui.message.chat;

import org.json.JSONArray;

public class chatObject {

    String chat_id;
    String userName;
    String lastMsg;
    String profileImg;

    JSONArray participants;

    public chatObject(String chat_id, String userName, String lastMsg, String profileImg, JSONArray participants) {
        this.chat_id = chat_id;
        this.userName = userName;
        this.lastMsg = lastMsg;
        this.profileImg = profileImg;
        this.participants = participants;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
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

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public JSONArray getParticipants() {
        return participants;
    }

    public void setParticipants(JSONArray participants) {
        this.participants = participants;
    }
}
