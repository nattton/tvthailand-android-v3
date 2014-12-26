package com.makathon.tvthailand.dao.advertise;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Random;

/**
 * Created by nattapong on 12/27/14 AD.
 */
public class AdCollectionDao {
    @SerializedName("delay_start") private int delayStart;
    @SerializedName("ads") public List<AdItemDao> ads;

    public int getDelayStart() {
        return delayStart;
    }

    public List<AdItemDao> getAds() {
        return ads;
    }

    public AdItemDao getShuffleAd() {
        int adSize = ads.size();
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(adSize);
        return ads.get(randomInt);
    }

}
