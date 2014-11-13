package com.makathon.tvthailand.datasource;

import java.util.ArrayList;
import java.util.HashMap;

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

public class Radios {
	
	private ArrayList<Radio> radios = new ArrayList<>();
	
	public interface OnRadioChangeListener {
		void onRadioChange(Radios radios);
	}
	
	private Context mContext;
	private RequestQueue mRequestQueue;
	
	public Radios(Context context) {
		this.mContext = context;
		mRequestQueue = MyVolley.getRequestQueue();
	}
	
	public Radios getInstance(Context context) {
        this.mContext = context;
		return AppUtility.getInstance().getRadios(this.mContext);
	}
	
	private OnRadioChangeListener onRadioChangeListener;
	
	public void setRadioChangeListener(OnRadioChangeListener onRadioChangeListener) {
		this.onRadioChangeListener = onRadioChangeListener;
	}
	
	public void load() {
		String url = String.format("%s/channel?device=android&time=%s", AppUtility.BASE_URL, AppUtility.getCurrentTime());
		JsonObjectRequest loadCategoryRequest = new JsonObjectRequest(Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		mRequestQueue.add(loadCategoryRequest);
	}
	
	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jArray = response.getJSONArray("radios");
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
		HashMap<String, Integer> categorySet = new HashMap<String, Integer>();
		int headerValue = 0;
		
		for (int i = 0; i < length; i++) {
			try {
				JSONObject jObjs = jArray.getJSONObject(i);
				String cate = jObjs.getString("category");
				int headerId = 0;
				if (categorySet.containsKey(cate)) {
					headerId = categorySet.get(cate);
				} 
				else {
					headerId = headerValue;
					categorySet.put(cate, headerValue);
					headerValue++;
				}
				insert(
						new Radio(jObjs.getString("id") ,
								jObjs.getString("title"),
								jObjs.getString("description"),
								jObjs.getString("thumbnail"),
								jObjs.getString("url"),
								jObjs.getString("category"),
								headerId)
						);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void insert(Radio radio) {
		radios.add(radio);
		notifyRadiosChanged();
	}

	private void notifyRadiosChanged() {
		if(this.onRadioChangeListener != null) {
			this.onRadioChangeListener.onRadioChange(this);
		}
	}
	
	public int size() {
		return radios.size();
	}
	
	public Radio get(int position) {
		return radios.get(position);
	}
	
	public void clear() {
		radios.clear();
		notifyRadiosChanged();
	}
	
	public ArrayList<Radio> getArray() {
		return this.radios;
	}
}
