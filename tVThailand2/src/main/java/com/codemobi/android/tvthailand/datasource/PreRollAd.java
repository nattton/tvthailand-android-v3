package com.codemobi.android.tvthailand.datasource;

/**
 * Created by nattapong on 11/13/14 AD.
 */
public class PreRollAd {
    private String name;
    private String url;
    private int skipTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSkipTime() {
        return skipTime;
    }

    public void setSkipTime(int skipTime) {
        this.skipTime = skipTime;
    }
}
