package com.makathon.tvthailand.dao.show;

import com.google.gson.annotations.SerializedName;
import com.makathon.tvthailand.manager.bus.BusProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nattapong on 12/20/14 AD.
 */
public class ShowCollectionDao {
    @SerializedName("programs") private List<ShowItemDao> shows;

    public ShowCollectionDao() {
        this.shows = new ArrayList<>();
    }

    public List<ShowItemDao> getShows() {
        return shows;
    }

    public void setShows(List<ShowItemDao> shows) {
        this.shows = shows;
    }

    public void addShows(List<ShowItemDao> shows) {
        for (ShowItemDao item: shows) {
            this.shows.add(item);
        }

        BusProvider.getInstance().post(this);
    }
}
