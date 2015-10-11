package com.codemobi.android.tvthailand.dao.advertise;

import com.google.gson.annotations.SerializedName;

public class PreRollAdDao {
    @SerializedName("name") private String name;
    @SerializedName("url") private String url;
    @SerializedName("skip_time") private int skipTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSkipTime() {
        if (skipTime == 0)
            return skipTime;
        return 7000;
    }

    public void setSkipTime(int skipTime) {
        this.skipTime = skipTime;
    }
}
