package com.example.tenarse.ui.active_chat;

public class MessageObject {
    String idEmitter;
    String userName;
    String message;

    public MessageObject(String idEmitter, String userName, String message) {
        this.idEmitter = idEmitter;
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIdEmitter() {
        return idEmitter;
    }

    public void setIdEmitter(String idEmitter) {
        this.idEmitter = idEmitter;
    }
}

