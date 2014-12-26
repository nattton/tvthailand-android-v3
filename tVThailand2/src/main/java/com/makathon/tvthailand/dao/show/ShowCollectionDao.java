package com.makathon.tvthailand.dao.show;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nattapong on 12/20/14 AD.
 */
public class ShowCollectionDao {
    @SerializedName("programs") public List<ShowItemDao> shows;
}
