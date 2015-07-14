package com.codemobi.android.tvthailand.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.text.format.Time;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.codemobi.android.tvthailand.MyVolley;

/**
 * Created by nattapong
 */
public class Utils {

    private static Utils instance;

    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();
        return instance;
    }

    private Context mContext;

    private Utils() {
        mContext = Contextor.getInstance().getContext();
    }

    public String getCurrentTime() {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        return today.format("%Y%m%d%H%M");
    }

    public String getDeviceId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getVersionName() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }

    public void viewEpisode(String programId) {
        String url = String.format("%s/view_episode/%s?device=android",
                Constant.BASE_URL, programId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        stringRequest.setShouldCache(false);
        MyVolley.getRequestQueue().add(stringRequest);
    }

}
