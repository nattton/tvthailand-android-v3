package com.codemobi.android.tvthailand.dao.section;

import com.google.gson.annotations.SerializedName;

public class ChannelItemDao {
    @SerializedName("id") private String id;
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("thumbnail") private String thumbnail;
    @SerializedName("url") private String url;
    @SerializedName("has_show") private String hasShow;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public String getHasShow() {
        return hasShow;
    }
}
