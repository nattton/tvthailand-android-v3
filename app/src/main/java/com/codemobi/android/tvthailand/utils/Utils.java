package com.codemobi.android.tvthailand.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.provider.Settings;
import android.text.format.Time;

import com.codemobi.android.tvthailand.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

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

    public String getDeviceId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void viewEpisode(String programId) {

        String url = String.format("%s/view_episode/%s?device=android", Constant.BASE_URL, programId);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }

}
