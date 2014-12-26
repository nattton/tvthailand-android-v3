package com.makathon.tvthailand.otv.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.makathon.tvthailand.datasource.OnLoadDataListener;

public class OTVShows {
	
	private static String EMPTY_STRING = "";
	
	private ArrayList<OTVShow> shows = new ArrayList<>();

	protected void jsonMap(JSONObject jObject) {
		
		try {
			JSONArray jArray = jObject.getJSONArray("items");
			
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jObj = jArray.getJSONObject(i);
				insert( new OTVShow(
						jObj.has("id") ? jObj.getString("id") : EMPTY_STRING,
						jObj.has("name_th") ? jObj.getString("name_th") : EMPTY_STRING,
						jObj.has("name_en") ? jObj.getString("name_en") : EMPTY_STRING,
						jObj.has("detail") ? jObj.getString("detail") : EMPTY_STRING,
						jObj.has("director") ? jObj.getString("director") : EMPTY_STRING,
						jObj.has("writer") ? jObj.getString("writer") : EMPTY_STRING,
						jObj.has("actor") ? jObj.getString("actor") : EMPTY_STRING,
						jObj.has("genre") ? jObj.getString("genre") : EMPTY_STRING, 
						jObj.has("release") ? jObj.getString("release") : EMPTY_STRING, 
						jObj.has("rate") ? jObj.getString("rate") : EMPTY_STRING, 
						jObj.has("rating") ? jObj.getString("rating") : EMPTY_STRING, 
						jObj.has("thumbnail") ? jObj.getString("thumbnail") : EMPTY_STRING, 
						jObj.has("cover") ? jObj.getString("cover") : EMPTY_STRING));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	private void insert(OTVShow otvShow) {
		shows.add(otvShow);
		notifyOTVShowsChanged();
	}
	
	public void clear() {
		shows.clear();
		notifyOTVShowsChanged();
	}

	public int size() {
		return shows.size();
	}
	
	public OTVShow get(int position) {
		return shows.get(position);
	}
	
	public interface OnOTVShowsChangeListener {
		void OnOTVShowsChange(OTVShows otvShows);
	}
	
	private OnOTVShowsChangeListener onOTVShowsChangeListener;

	public void setOnOTVShowsChangeListener(
			OnOTVShowsChangeListener onOTVShowsChangeListener) {
		this.onOTVShowsChangeListener = onOTVShowsChangeListener;
	}
	
	private void notifyOTVShowsChanged() {
		if (this.onOTVShowsChangeListener != null) {
			this.onOTVShowsChangeListener.OnOTVShowsChange(this);
		}
	}
	
	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
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
