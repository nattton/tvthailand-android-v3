package com.makathon.tvthailand.otv.datasoruce;

import io.vov.vitamio.utils.Log;

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
import com.makathon.tvthailand.Application;
import com.makathon.tvthailand.MyVolley;

import android.content.Context;

public class OTVCategories extends android.app.Application {
	private RequestQueue mRequestQueue;
	private static OTVCategories instance;
	private ArrayList<OTVCategory> categories = new ArrayList<OTVCategory>();

	public static synchronized OTVCategories getInstance() {
		if (instance == null) {
			instance = new OTVCategories();
		}
		return instance;
	}

	public void loadOTVCategories(final Context context) {
		mRequestQueue = MyVolley.getRequestQueue();
		String url = String.format("%s/CategoryList/index/%s/%s/%s/",
				OTVConfig.BASE_URL, OTVConfig.APP_ID,
				Application.getAppVersion(), OTVConfig.API_VERSION);
		JsonObjectRequest loadEpisodeRequest = new JsonObjectRequest(
				Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		mRequestQueue.add(loadEpisodeRequest);
		Log.e("url", url);
	}

	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				clear();
				jsonMap(response);
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

	public void jsonMap(JSONObject jsonObject) {

		try {
			JSONArray jArray = jsonObject.getJSONArray("items");

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObj = jArray.getJSONObject(i);
				insert(new OTVCategory(jObj.getString("id"),
						jObj.getString("api_name"), jObj.getString("name_th"),
						jObj.getString("name_en"),
						jObj.getString("description")));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void insert(OTVCategory otvCategory) {
		categories.add(otvCategory);
		notifyOTVCategoriesChanged();
	}

	public void clear() {
		categories.clear();
		notifyOTVCategoriesChanged();
	}

	public int size() {
		return categories.size();
	}

	public OTVCategory get(int position) {
		return categories.get(position);
	}

	public interface OnOTVCategoriesChangeListener {
		void OnOTVCategoriesChange(OTVCategories otvCategories);
	}

	private OnOTVCategoriesChangeListener onOTVCategoriesChangeListener;

	public void setOnOTVCategoriesChangeListener(
			OnOTVCategoriesChangeListener onOTVCategoriesChangeListener) {
		this.onOTVCategoriesChangeListener = onOTVCategoriesChangeListener;
	}

	private void notifyOTVCategoriesChanged() {
		if (this.onOTVCategoriesChangeListener != null) {
			this.onOTVCategoriesChangeListener.OnOTVCategoriesChange(this);
		}
	}

}
