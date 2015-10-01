package com.codemobi.android.tvthailand.dao.section;

import com.google.gson.annotations.SerializedName;

public class RadioItemDao {
    @SerializedName("id") private String id;
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;
    @SerializedName("thumbnail") private String thumbnail;
    @SerializedName("url") private String url;
    @SerializedName("category") private String category;
    private int headerId;

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

    public String getCategory() {
        return category;
    }

    public int getHeaderId() {
        return headerId;
    }

    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }
}
