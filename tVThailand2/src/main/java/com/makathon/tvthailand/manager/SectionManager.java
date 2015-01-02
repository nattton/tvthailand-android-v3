package com.makathon.tvthailand.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.makathon.tvthailand.dao.section.SectionCollectionDao;
import com.makathon.tvthailand.manager.bus.MainBus;
import com.makathon.tvthailand.utils.Constant;
import com.makathon.tvthailand.utils.Contextor;

import java.util.HashMap;


/**
 * Created by nattapong.
 */
public class SectionManager {

    public static enum EventType {
        Loaded
    }

    private static SectionManager instance;

    public static SectionManager getInstance() {
        if (instance == null)
            instance = new SectionManager();
        return instance;
    }

    private Context mContext;

    private SectionCollectionDao data;

    private SectionManager() {
        mContext = Contextor.getInstance().getContext();
    }

    public SectionCollectionDao getData() {
        if (data == null) {
            data = new SectionCollectionDao();
        }
        return data;
    }

    public void setData(SectionCollectionDao data) {
        if (data != null && data.getRadios() != null) {
            HashMap<String, Integer> categorySet = new HashMap<>();
            int headerValue = 0;
            for (int i = 0; i < data.getRadios().size(); i++) {
                String cate = data.getRadios().get(i).getCategory();
                int headerId = 0;
                if (categorySet.containsKey(cate)) {
                    headerId = categorySet.get(cate);
                } else {
                    headerId = headerValue;
                    categorySet.put(cate, headerValue);
                    headerValue++;
                }
                data.getRadios().get(i).setHeaderId(headerId);
            }
        }
        this.data = data;
        MainBus.getInstance().post(EventType.Loaded);
        saveData();
    }

    public void saveData() {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        SharedPreferences pref = mContext.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constant.PREF_SECTION, json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences pref = mContext.getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE);
        String json = pref.getString(Constant.PREF_SECTION, "");
        if (json.equals(""))
            return;
        Gson gson = new Gson();
        data = gson.fromJson(json, SectionCollectionDao.class);
    }
}
