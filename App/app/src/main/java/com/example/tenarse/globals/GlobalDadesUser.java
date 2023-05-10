package com.example.tenarse.globals;

import org.json.JSONObject;
import java.util.List;

public class GlobalDadesUser {
    private static GlobalDadesUser instance;
    private JSONObject dadesUser;
    private List<Object> dataList;
    private boolean isFirstEntry = true;

    private GlobalDadesUser() {}

    public static synchronized GlobalDadesUser getInstance() {
        if (instance == null){
            instance = new GlobalDadesUser();
        }
        return instance;
    }

    public boolean isFirstEntry() {
        return isFirstEntry;
    }

    public void setFirstEntry(boolean firstEntry) {
        isFirstEntry = firstEntry;
    }

    public JSONObject getDadesUser() {
        return dadesUser;
    }

    public void setDadesUser(JSONObject dadesUser) {
        this.dadesUser = dadesUser;
    }

    public List<Object> getDataList() {
        return dataList;
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }
}