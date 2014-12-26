package com.makathon.tvthailand.dao.advertise;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nattapong on 12/27/14 AD.
 */
public class KapookItemDao {
    @SerializedName("url_1x1") private String url1x1;
    @SerializedName("url_show") private String urlShow;

    public String getUrl1x1() {
        return url1x1;
    }

    public String getUrlShow() {
        return urlShow;
    }
}
