package com.makathon.tvthailand.datasource;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.makathon.tvthailand.MyVolley;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class Section {
	private static final String PREF_NAME = "com.makathon.tvthailand.datasource.Section";
	private static final String PREF_SECTION_TIME = "PREF_SECTION_TIME";
	private static final String PREF_CAT_NAME = "PREF_CAT_NAME";
	private static final String PREF_CH_NAME = "PREF_CH_NAME";
	private static final String PREF_RADIO_NAME = "PREF_RADIO_NAME";
	private static final long SECTION_TIME = 3600000l;

	private Handler mHander = new Handler();
	
	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
	}

	private RequestQueue mRequestQueue;
	
	private Context mContext;
	private Categories mCategories;
	private Channels mChannels;
	private Radios mRadios;

	public Section(Context context) {
		this.mContext = context;
		mRequestQueue = MyVolley.getRequestQueue();
		
		this.mCategories = AppUtility.getInstance().getCategories(this.mContext);
		this.mChannels = AppUtility.getInstance().getChannels(this.mContext);
		this.mRadios = AppUtility.getInstance().getRadios(this.mContext);
	}

	public void load() {
		load(false);
	}
	public void load(boolean isForce) {
		loadSection(!false);
		
//		SharedPreferences prefs = mContext.getSharedPreferences(PREF_NAME,
//				Context.MODE_PRIVATE);
//		long time = prefs.getLong(PREF_SECTION_TIME, 0);
//		Date nextUpdateTime = new Date(time + SECTION_TIME);
//		Date currentTime = new Date();
////		Log.d("loadSection", "currentTime : " + currentTime);
////		Log.d("loadSection", "nextUpdateTime : " + nextUpdateTime);
//
//		String cateStr = prefs.getString(PREF_CAT_NAME, "");
//		String chStr = prefs.getString(PREF_CH_NAME, "");
//		String radioStr = prefs.getString(PREF_RADIO_NAME, "");
//		
//		if (!cateStr.equals("")) {
//			try {
//				JSONArray cateArrPref = new JSONArray(cateStr);
//				mCategories.clear();
//				mCategories.jsonMap(cateArrPref);
//			} catch (JSONException e) {
//			}
//		}
//		
//		if(!chStr.equals("")) {
//			try {
//				JSONArray chArrPref = new JSONArray(chStr);
//				mChannels.clear();
//				mChannels.jsonMap(chArrPref);
//			} catch (JSONException e) {
//			}
//		}
//		
//		if(!radioStr.equals("")) {
//			try {
//				JSONArray radioStrPref = new JSONArray(radioStr);
//				mRadios.clear();
//				mRadios.jsonMap(radioStrPref);
//			} catch (JSONException e) {
//			}
//		}
//
//		if (isForce || currentTime.after(nextUpdateTime)) {
//			Log.e("load Section", "Start Load Program");
//			loadSection();
//		}
		
	}
	
	private void loadSection(boolean shouldCache) {
		notifyLoadStart();
		String url = String.format("%s/section?device=android&time=%s", AppUtility.BASE_URL, AppUtility.getCurrentTime());
		JsonObjectRequest loadSectionRequest = new JsonObjectRequest(Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		loadSectionRequest.setShouldCache(shouldCache);
		mRequestQueue.add(loadSectionRequest);
	}
	
	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray catArr = response.getJSONArray("categories");
					JSONArray chArr = response.getJSONArray("channels");
					JSONArray radioArr = response.getJSONArray("radios");
					
					mCategories.jsonMap(catArr);
					mChannels.jsonMap(chArr);
					mRadios.jsonMap(radioArr);
					
					SharedPreferences prefs = mContext.getSharedPreferences(
							PREF_NAME, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(PREF_CAT_NAME, catArr.toString());
					editor.putString(PREF_CH_NAME, chArr.toString());
					editor.putString(PREF_RADIO_NAME, radioArr.toString());
					editor.putLong(PREF_SECTION_TIME, new Date().getTime());
					editor.commit();

				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					notifyLoadFinish();
				}
			}
		};
	}
	
	private Response.ErrorListener reqErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				notifyLoadFinish();
				
				Runnable mRunable = new Runnable() {
					
					@Override
					public void run() {
						load();
					}
				};
				
				mHander.postDelayed(mRunable, 30000);
			}
		};
	}

	private void notifyLoadStart() {
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadStart();
		}
	}

	private void notifyLoadFinish() {
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadFinished();
		}
	}

}
