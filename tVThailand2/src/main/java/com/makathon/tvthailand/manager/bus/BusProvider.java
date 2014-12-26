package com.makathon.tvthailand.manager.bus;

import com.squareup.otto.Bus;

/**
 * Created by nattapong on 12/19/14 AD.
 */
public class BusProvider {
    private static Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {

    }

}
