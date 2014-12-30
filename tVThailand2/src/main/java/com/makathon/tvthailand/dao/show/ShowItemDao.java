package com.makathon.tvthailand.dao.show;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nattapong on 12/20/14 AD.
 */
public class ShowItemDao {
    @SerializedName("id") private String id;
    @SerializedName("title") private String title;
    @SerializedName("thumbnail") private String thumbnailURL;
    @SerializedName("description") private String description;
    @SerializedName("last_epname") private String lastEpname;
    @SerializedName("is_otv") private String isOtv;
    @SerializedName("otv_id") private String otvID;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getDescription() {
        return description;
    }

    public String getLastEpname() {
        return lastEpname;
    }

    public boolean isOtv() {
        return "1".equals(isOtv);
    }

    public String getOtvID() {
        return otvID;
    }
}
