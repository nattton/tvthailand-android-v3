package com.codemobi.android.tvthailand.datasource;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codemobi.android.tvthailand.MyVolley;
import com.codemobi.android.tvthailand.utils.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import io.vov.vitamio.utils.Log;

public class PreRollAdFactory {

    public interface OnLoadListener {
        void onStart();
        void onFinish();
    }

    private OnLoadListener onLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }
    
    private RequestQueue mRequestQueue;
    private ArrayList<PreRollAd> preRollAds = new ArrayList<>();

    public PreRollAdFactory(Context context){
        this.mRequestQueue = MyVolley.getInstance(context).getRequestQueue();
    }

    public PreRollAd getPreRollAd() {
        int size = preRollAds.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return preRollAds.get(0);
        } else {
            Random rand = new Random();
            int randomNum = rand.nextInt(size);
            return preRollAds.get(randomNum);
        }
    }

    public void load(){
        notifyLoadStart();
        String url = String.format("%s/preroll_advertise?device=android", Constant.BASE_URL);
        JsonObjectRequest loadRequest = new JsonObjectRequest(Request.Method.GET, url, reqSuccessListener(), reqErrorListener());
        mRequestQueue.add(loadRequest);
    }

    private Response.Listener<JSONObject> reqSuccessListener() {
        return new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("ads")){
                        preRollAds.clear();
                        JSONArray ads = response.getJSONArray("ads");
                        for(int i = 0; i < ads.length(); i++){
                            JSONObject ad = ads.getJSONObject(i);
                            PreRollAd preRollAd = new PreRollAd();
                            preRollAd.setName(ad.getString("name"));
                            preRollAd.setUrl(ad.getString("url"));
                            preRollAd.setSkipTime(ad.has("skip_time")?ad.getInt("skip_time"): 7000);
                            preRollAds.add(preRollAd);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Get Pre-Roll Ads", e.getMessage());
                } finally {
                    notifyLoadFinish();
                }
            }
        };
    }

    private Response.ErrorListener reqErrorListener () {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                notifyLoadFinish();
            }
        };
    }

    private void notifyLoadStart() {
        if (onLoadListener != null) {
            onLoadListener.onStart();
        }
    }

    private void notifyLoadFinish() {
        if (onLoadListener != null) {
            onLoadListener.onFinish();
        }
    }
}
