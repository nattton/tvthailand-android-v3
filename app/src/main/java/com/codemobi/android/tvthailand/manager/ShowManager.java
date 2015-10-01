package com.codemobi.android.tvthailand.manager;

import android.content.Context;

import com.codemobi.android.tvthailand.dao.show.ShowCollectionDao;
import com.codemobi.android.tvthailand.dao.show.ShowItemDao;
import com.codemobi.android.tvthailand.manager.bus.MainBus;
import com.codemobi.android.tvthailand.utils.Contextor;


/**
 * Created by nattapong.
 */
public class ShowManager {

    public static enum EventType {
        Loaded
    }

    private static ShowManager instance;

    public static ShowManager getInstance() {
        if (instance == null)
            instance = new ShowManager();
        return instance;
    }

    private Context mContext;

    private ShowCollectionDao data = new ShowCollectionDao();

    private ShowManager() {
        mContext = Contextor.getInstance().getContext();
    }

    public ShowCollectionDao getData() {
        return data;
    }

    public void setData(int start, ShowCollectionDao data) {
        if (start == 0)
            clear();

        for (ShowItemDao item: data.getShows()) {
            this.data.getShows().add(item);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                MainBus.getInstance().post(EventType.Loaded);
            }
        });
    }

    public void clear() {
        this.data.getShows().clear();
    }

}
