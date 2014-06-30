package com.makathon.tvthailand.datasource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.makathon.tvthailand.MyVolley;

import android.content.Context;


public class Categories {
	
	private RequestQueue mRequestQueue;
	
	private ArrayList<Category> categories = new ArrayList<Category>();
	
	public interface OnCategoryChangeListener {
		void onCategoryChange(Categories categories);
	}
	
	Context mContext;
	public Categories(Context context) {
		mContext = context;
		mRequestQueue = MyVolley.getRequestQueue();
	}
	
	public Categories getInstance(Context context) {
		return AppUtility.getInstance().getCategories(this.mContext);
	}
	
	private OnCategoryChangeListener onCategoryChangeListener;
	
	public void setOnCategoryChangeListener(OnCategoryChangeListener onCategoryChangeListener) {
		this.onCategoryChangeListener = onCategoryChangeListener;
	}
	
	public void loadCategory() {	
		String url = String.format("%s/category?device=android", AppUtility.BASE_URL);
		JsonObjectRequest loadCategoryRequest = new JsonObjectRequest(Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		mRequestQueue.add(loadCategoryRequest);
	}

	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					JSONArray jArray = response.getJSONArray("categories");
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
				JSONObject jObj = jArray.getJSONObject(i);
				insert(
						new Category(jObj.getString("id") ,
								jObj.getString("title"),
								jObj.getString("description"),
								jObj.getString("thumbnail"))
						);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void insert(Category category) {
		categories.add(category);
		notifyCategoriesChanged();
	}

	private void notifyCategoriesChanged() {
		if(this.onCategoryChangeListener != null) {
			this.onCategoryChangeListener.onCategoryChange(this);
		}
	}
	
	public int size() {
		return categories.size();
	}
	
	public Category get(int position) {
		return categories.get(position);
	}
	
	public void clear() {
		categories.clear();
		notifyCategoriesChanged();
	}
}
