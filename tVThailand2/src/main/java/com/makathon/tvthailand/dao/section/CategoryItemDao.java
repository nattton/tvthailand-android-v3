package com.makathon.tvthailand.dao.section;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nattapong on 12/19/14 AD.
 */
public class CategoryItemDao {
    @SerializedName("id") private String id;
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("thumbnail") private String thumbnail;

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
}
