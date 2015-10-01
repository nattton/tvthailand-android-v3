package com.codemobi.android.tvthailand.utils;

import android.content.Context;

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
