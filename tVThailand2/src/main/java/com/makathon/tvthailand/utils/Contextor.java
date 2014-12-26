package com.makathon.tvthailand.utils;

import android.content.Context;

/**
 * Created by nattapong on 12/18/14 AD.
 */
public class Contextor {

    private static Contextor instance;

    public static Contextor getInstance() {
        if (instance == null) {
            instance = new Contextor();
        }
        return instance;
    }

    private Context mContext;

    public Contextor() { }

    public void init(Context context) {
        mContext = context;
    }

    public  Context getContext() {
        return mContext;
    }

}
