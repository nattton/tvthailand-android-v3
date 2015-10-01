package com.codemobi.android.tvthailand.manager.http;

/**
 * Created by nattapong on 12/27/14 AD.
 *
 */
public interface HTTPCallback<T> {

    public void onFailure();

    public void onResponse(T data);
}
