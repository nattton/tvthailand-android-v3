package com.makathon.tvthailand.datasource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.makathon.tvthailand.MyVolley;

import android.content.Context;

public class Channels {
	
	private ArrayList<Channel> channels = new ArrayList<Channel>();
	
	public interface OnChannelChangeListener {
		void onChannelChange(Channels channels);
	}
	
	Context mContext;
	private RequestQueue mRequestQueue;
	
	public Channels(Context context) {
		this.mContext = context;
		mRequestQueue = MyVolley.getRequestQueue();
	}
	
	public Channels getInstance(Context context) {
		return AppUtility.getInstance().getChannels(this.mContext);
	}
	
	private OnChannelChangeListener onChannelChangeListener;
	
	public void setChannelChangeListener(OnChannelChangeListener onChannelChangeListener) {
		this.onChannelChangeListener = onChannelChangeListener;
	}
	
	public void load() {
		String url = String.format("%s/channel?device=android", AppUtility.BASE_URL);
		JsonObjectRequest loadCategoryRequest = new JsonObjectRequest(Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		mRequestQueue.add(loadCategoryRequest);
	}
	
	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jArray = response.getJSONArray("channels");
					jsonMap(jArray);
				} catch (Exception e) {

				}
			}
		};
	}
	
	private ErrorListener reqErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		};
	}
	
	public void jsonMap(JSONArray jArray) {
		clear();
		int length = jArray.length();
		for (int i = 0; i < length; i++) {
			try {
				JSONObject jObjs = jArray.getJSONObject(i);
				insert(
						new Channel(jObjs.getString("id") ,
								jObjs.getString("title"),
								jObjs.getString("description"),
								jObjs.getString("thumbnail"),
								jObjs.getString("url"))
						);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void insert(Channel channel) {
		channels.add(channel);
		
		notifyChannelsChanged();
	}

	private void notifyChannelsChanged() {
		if(this.onChannelChangeListener != null) {
			this.onChannelChangeListener.onChannelChange(this);
		}
	}
	
	public int size() {
		return channels.size();
	}
	
	public Channel get(int position) {
		return channels.get(position);
	}
	
	public void clear() {
		channels.clear();
		notifyChannelsChanged();
	}
}
