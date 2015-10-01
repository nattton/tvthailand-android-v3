package com.codemobi.android.tvthailand.manager.bus;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by nattapong on 12/19/14 AD.
 * Bus Otto
 */
public class MainBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private static MainBus instance;

    public static MainBus getInstance() {
        if (instance == null)
            instance = new MainBus();
        return instance;
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainBus.super.post(event);
                }
            });
        }
    }

}
