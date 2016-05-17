package com.codemobi.android.tvthailand.dao.advertise;

import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.codemobi.android.tvthailand.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PreRollAdFactory {

    public interface OnLoadListener {
        void onStart();
        void onFinish();
    }

    private OnLoadListener onLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    private List<PreRollAdDao> preRollAds = new ArrayList<>();

    public PreRollAdFactory(){

    }

    public PreRollAdDao getPreRollAd() {
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
        Call<PreRollAdCollectionDao> call = APIClient.getClient().loadPreRollAd(Constant.defaultParams);
        call.enqueue(new Callback<PreRollAdCollectionDao>() {
            @Override
            public void onResponse(Call<PreRollAdCollectionDao> call, Response<PreRollAdCollectionDao> response) {
                if (response.isSuccessful()) {
                    preRollAds.clear();
                    PreRollAdCollectionDao preRollAdCollectionDao = response.body();
                    preRollAds = preRollAdCollectionDao.ads;
                }
                notifyLoadFinish();
            }

            @Override
            public void onFailure(Call<PreRollAdCollectionDao> call, Throwable t) {
                notifyLoadFinish();
            }
        });
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
