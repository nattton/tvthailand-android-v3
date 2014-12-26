package com.makathon.tvthailand.manager.http;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.makathon.tvthailand.dao.advertise.AdCollectionDao;
import com.makathon.tvthailand.dao.advertise.KapookItemDao;
import com.makathon.tvthailand.dao.section.SectionCollectionDao;
import com.makathon.tvthailand.dao.show.ShowCollectionDao;
import com.makathon.tvthailand.utils.Constant;
import com.makathon.tvthailand.utils.Contextor;


/**
 * Created by nattapong.
 */
public class HTTPEngine {

    private static HTTPEngine instance;

    public static HTTPEngine getInstance() {
        if (instance == null)
            instance = new HTTPEngine();
        return instance;
    }

    private Context mContext;

    private HTTPEngine() {
        mContext = Contextor.getInstance().getContext();
    }

    public void getAdvertiseData(FutureCallback<AdCollectionDao> callback) {
        String url = String.format("%s/advertise", Constant.BASE_URL);
        Ion.with(mContext)
                .load(url)
                .addQueries(Constant.getDefaultParams())
                .setLogging("Load Advertise", 1)
                .as(new TypeToken<AdCollectionDao>() {})
                .setCallback(callback);
    }

    public void getAdKapookData (FutureCallback<KapookItemDao> callback) {
        String url = "http://kapi.kapook.com/partner/url";
        Ion.with(mContext)
                .load(url)
                .setLogging("Load AdKapook", 1)
                .as(new TypeToken<KapookItemDao>() {})
                .setCallback(callback);
    }
    public void getSectionData(FutureCallback<SectionCollectionDao> callback) {
        String url = String.format("%s/section", Constant.BASE_URL);
        Ion.with(mContext)
                .load(url)
                .addQueries(Constant.getDefaultParams())
                .setLogging("Load Section", 1)
                .as(new TypeToken<SectionCollectionDao>() {})
                .setCallback(callback);
    }

    public void getCategory(String categoryID, int start, FutureCallback<ShowCollectionDao> callback) {
        String url = String.format("%s/category/%s/%d", Constant.BASE_URL, categoryID, start);
        Ion.with(mContext)
                .load(url)
                .addQueries(Constant.getDefaultParams())
                .setLogging("Load Category", 1)
                .as(new TypeToken<ShowCollectionDao>() {})
                .setCallback(callback);
    }
}
