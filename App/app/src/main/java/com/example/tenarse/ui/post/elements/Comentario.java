package com.example.tenarse.ui.post.elements;

import java.io.Serializable;

public class Comentario implements Serializable {

    public String com_user_img;
    public String com_username;
    public String com_text;

    public Comentario(String com_user_img, String com_username, String com_text) {
        this.com_user_img = com_user_img;
        this.com_username = com_username;
        this.com_text = com_text;
    }

    public String getCom_user_img() {
        return com_user_img;
    }

    public void setCom_user_img(String com_user_img) {
        this.com_user_img = com_user_img;
    }

    public String getCom_username() {
        return com_username;
    }

    public void setCom_username(String com_username) {
        this.com_username = com_username;
    }

    public String getCom_text() {
        return com_text;
    }

    public void setCom_text(String com_text) {
        this.com_text = com_text;
    }
}
