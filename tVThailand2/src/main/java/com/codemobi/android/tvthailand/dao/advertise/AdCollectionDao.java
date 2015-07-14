package com.codemobi.android.tvthailand.dao.advertise;

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

    public AdItemDao getShuffleAd() throws EmptyException {
        int adSize = ads.size();
        if (adSize == 0) throw new EmptyException("Empty List");
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(adSize);
        return ads.get(randomInt);
    }

    public static class EmptyException extends Exception {
        public EmptyException(String msg) {
            super(msg);
        }
    }
}
