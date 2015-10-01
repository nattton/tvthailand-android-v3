package com.codemobi.android.tvthailand.manager;

import android.content.Context;

import com.codemobi.android.tvthailand.utils.Contextor;


/**
 * Created by nattapong.
 */
public class SingletonTemplate {

    private static SingletonTemplate instance;

    public static  SingletonTemplate getInstance() {
        if (instance == null)
            instance = new SingletonTemplate();
        return instance;
    }

    private Context mContext;

    private SingletonTemplate() {
        mContext = Contextor.getInstance().getContext();
    }

}
