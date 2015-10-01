package com.codemobi.android.tvthailand.dao;

import com.google.gson.annotations.SerializedName;

public class TestDao extends BaseDao {

    @SerializedName("message") private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
