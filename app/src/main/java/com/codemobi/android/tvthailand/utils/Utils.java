package com.codemobi.android.tvthailand.utils;

import android.content.Context;
import android.provider.Settings;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

}
