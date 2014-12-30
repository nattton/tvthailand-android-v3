package com.makathon.tvthailand.manager.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makathon.tvthailand.MyVolley;
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
    private RequestQueue mRequestQueue;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public enum ShowMode {
        CATEGORY, CHANNEL
    }

    private HTTPEngine() {
        mContext = Contextor.getInstance().getContext();
        mRequestQueue = MyVolley.getRequestQueue();
    }

    public void getAdvertiseData(Response.Listener<AdCollectionDao> listener, Response.ErrorListener errorListener) {
        String url = String.format("%s/advertise", Constant.BASE_URL);
        GsonRequest<AdCollectionDao> gsonRequest = new GsonRequest<>(url, AdCollectionDao.class, null, listener, errorListener);
        mRequestQueue.add(gsonRequest);
    }

    public void getAdKapookData(Response.Listener<KapookItemDao> listener, Response.ErrorListener errorListener) {
        String url = "http://kapi.kapook.com/partner/url";
        GsonRequest<KapookItemDao> gsonRequest = new GsonRequest<>(url, KapookItemDao.class, null, listener, errorListener);
        mRequestQueue.add(gsonRequest);
    }

    public void getSectionData(Response.Listener<SectionCollectionDao> listener, Response.ErrorListener errorListener) {
        String url = String.format("%s/section", Constant.BASE_URL);
        GsonRequest<SectionCollectionDao> gsonRequest = new GsonRequest<>(url, SectionCollectionDao.class, null, listener, errorListener);
        mRequestQueue.add(gsonRequest);
    }

    public void getShowData(ShowMode showMode, String id, final int start, Response.Listener<ShowCollectionDao> listener, Response.ErrorListener errorListener) {
        String apiName = "";
        switch (showMode) {
            case CATEGORY:
                apiName = "category";
                break;
            case CHANNEL:
                apiName = "channel";
                break;
        }

        String url = String.format("%s/%s/%s/%d", Constant.BASE_URL, apiName, id, start);
        GsonRequest<ShowCollectionDao> gsonRequest = new GsonRequest<>(url, ShowCollectionDao.class, null, listener, errorListener);
        mRequestQueue.add(gsonRequest);
    }
}
