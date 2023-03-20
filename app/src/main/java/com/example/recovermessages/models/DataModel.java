package com.example.recovermessages.models;

public class DataModel {
    private String msg;
    private String time;

    public DataModel(String str, String str2) {
        this.msg = str;
        this.time = str2;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String str) {
        this.time = str;
    }
}
