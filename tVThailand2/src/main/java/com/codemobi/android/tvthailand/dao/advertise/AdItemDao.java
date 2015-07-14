package com.codemobi.android.tvthailand.dao.advertise;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nattapong on 12/27/14 AD.
 */
public class AdItemDao {
    @SerializedName("name") private String name;
    @SerializedName("url") private String url;
    @SerializedName("time") private int time;
    @SerializedName("interval") private int interval;

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getTime() {
        return time;
    }

    public int getInterval() {
        return interval;
    }
}
