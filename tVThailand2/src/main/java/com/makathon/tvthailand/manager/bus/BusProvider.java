package com.makathon.tvthailand.manager.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by nattapong on 12/19/14 AD.
 */
public class BusProvider {
    private static Bus instance = new Bus(ThreadEnforcer.MAIN);

    public static Bus getInstance() {
        return instance;
    }

    private BusProvider() {

    }

}
