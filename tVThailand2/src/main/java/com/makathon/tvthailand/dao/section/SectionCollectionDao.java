package com.makathon.tvthailand.dao.section;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nattapong on 12/19/14 AD.
 */
public class SectionCollectionDao {
    @SerializedName("categories") private CategoryItemDao[] categories;
    @SerializedName("channels") private ChannelItemDao[] channels;
    @SerializedName("radios") private RadioItemDao[] radios;

    public CategoryItemDao[] getCategories() {
        return categories;
    }

    public ChannelItemDao[] getChannels() {
        return channels;
    }

    public RadioItemDao[] getRadios() {
        return radios;
    }
}
