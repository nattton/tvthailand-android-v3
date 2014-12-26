package com.makathon.tvthailand.dao;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class BaseDao {

    @SerializedName("success") private boolean success;
    @SerializedName("reason") private String reason;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
