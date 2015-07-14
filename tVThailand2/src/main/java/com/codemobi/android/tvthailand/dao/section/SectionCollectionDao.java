package com.codemobi.android.tvthailand.dao.section;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * Created by nattapong on 12/19/14 AD.
 */
public class SectionCollectionDao {
    @SerializedName("categories") private List<CategoryItemDao> categories;
    @SerializedName("channels") private List<ChannelItemDao> channels;
    @SerializedName("radios") private List<RadioItemDao> radios;

    public List<CategoryItemDao> getCategories() {
        return categories;
    }

    public List<ChannelItemDao> getChannels() {
        return channels;
    }

    public List<RadioItemDao> getRadios() {
        if (radios == null) {
            radios = Collections.emptyList();
        }
        return radios;
    }
}
