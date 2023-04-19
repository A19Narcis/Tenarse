package com.example.tenarse.globals;

import org.json.JSONObject;

public class GlobalDadesUser {
    private static GlobalDadesUser instance;
    private JSONObject dadesUser;

    private GlobalDadesUser() {}

    public static synchronized GlobalDadesUser getInstance() {
        if (instance == null){
            instance = new GlobalDadesUser();
        }
        return instance;
    }

    public JSONObject getDadesUser() {
        return dadesUser;
    }

    public void setDadesUser(JSONObject dadesUser) {
        this.dadesUser = dadesUser;
    }

}
