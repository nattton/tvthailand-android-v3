package com.codemobi.android.tvthailand.otv.model;

import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codemobi.android.tvthailand.MainApplication;
import com.codemobi.android.tvthailand.MyVolley;
import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.otv.OTVConfig;
import com.codemobi.android.tvthailand.utils.Contextor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class OTVEpisodes {
	private static final String EMPTY_STRING = "";

	private ArrayList<OTVEpisode> episodes = new ArrayList<>();
	
	public OTVEpisodes() {

	}

	public void loadOTVEpisodes(Program show) {
		notifyLoadStart();
		String url = String.format("%s/Content/index/%s/%s/%s/%s/0/50/0", OTVConfig.BASE_URL, OTVConfig.APP_ID, MainApplication.getAppVersion(), OTVConfig.API_VERSION, show.getOtvId());
        Log.d("OTVEpisodes", url);
        JsonObjectRequest loadEpisodeRequest = new JsonObjectRequest(Method.GET, url, reqSuccessListener(), reqErrorListener());
		loadEpisodeRequest.setShouldCache(false);
        MyVolley.getInstance(Contextor.getInstance().getContext()).getRequestQueue().add(loadEpisodeRequest);
	}
	
	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				clear();
				jsonMap(response);
				notifyLoadFinish();
			}
		};
	}
	

	private ErrorListener reqErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				notifyLoadFinish();
			}
		};
	}

	protected void jsonMap(JSONObject jObject) {
		try {
			JSONArray jContentLists = jObject.getJSONArray("contentList");
			
			for (int i = 0; i < jContentLists.length(); i++) {
				JSONObject jContent = jContentLists.getJSONObject(i);
				
				ArrayList<OTVPart> parts = new ArrayList<>();
				JSONArray jArrayPart = jContent.getJSONArray("item");
				int count_part = jArrayPart.length();
				
				OTVPart part = null;
				for (int j = 0; j < count_part; j++) { 
					JSONObject jObjPart = jArrayPart.getJSONObject(j);
					if (part == null) {
						part = new OTVPart();	
					}
					
					if (jObjPart.has("media_code") && jObjPart.getString("media_code").equals("1001")) {
						part.setVastUrl(jObjPart.has("stream_url") ? jObjPart.getString("stream_url") : null);
					}
					else if (jObjPart.has("media_code")) {
						part.setPartId(jObjPart.has("id") ? jObjPart.getString("id") : EMPTY_STRING);
						part.setNameTh(jObjPart.has("name_th") ? jObjPart.getString("name_th") : EMPTY_STRING);
						part.setNameEn(jObjPart.has("name_en") ? jObjPart.getString("name_en") : EMPTY_STRING);
						part.setThumbnail(jObjPart.has("thumbnail") ? jObjPart.getString("thumbnail") : EMPTY_STRING);
						part.setCover(jObjPart.has("cover") ? jObjPart.getString("cover") : EMPTY_STRING);
						part.setStream_url(jObjPart.has("stream_url") ? jObjPart.getString("stream_url") : EMPTY_STRING);
						part.setMediaCode(jObjPart.getString("media_code"));
						
						parts.add(part);
						part = null;
					}
				}
				
				insert(new OTVEpisode(jContent.has("content_id") ? jContent.getString("content_id") : EMPTY_STRING,
						jContent.has("name_th") ? jContent.getString("name_th") : EMPTY_STRING, 
						jContent.has("name_en") ? jContent.getString("name_en") : EMPTY_STRING,
						jContent.has("detail") ? jContent.getString("detail") : EMPTY_STRING,
						jContent.has("thumbnail") ? jContent.getString("thumbnail") : EMPTY_STRING,
						jContent.has("cover") ? jContent.getString("cover") : EMPTY_STRING,
						jContent.has("rating_status") ? jContent.getString("rating_status") : EMPTY_STRING,
						jContent.has("rating_point") ? jContent.getString("rating_point") : EMPTY_STRING,
						jContent.has("date") ? jContent.getString("date") : EMPTY_STRING,
						parts));
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void insert(OTVEpisode otvEpisode) {
		episodes.add(otvEpisode);
		notifyOTVEpisodesChanged();
	}
	
	public void clear() {
		episodes.clear();
		notifyOTVEpisodesChanged();
	}
	
	public int size() {
		return episodes.size();
	}
	
	public OTVEpisode get(int position) {
		return episodes.get(position);
	}
	
	
	public interface OnOTVEpisodesChangeListener {
		void OnOTVEpisodesChange(OTVEpisodes otvEpisodes);
	}
	
	private OnOTVEpisodesChangeListener onOTVEpisodesChangeListener;
	
	public void setOnOTVEpisodesChangeListener(OnOTVEpisodesChangeListener onOTVEpisodesChangeListener) {
		this.onOTVEpisodesChangeListener = onOTVEpisodesChangeListener;
	}
	
	private void notifyOTVEpisodesChanged() {
		if (this.onOTVEpisodesChangeListener != null) {
			this.onOTVEpisodesChangeListener.OnOTVEpisodesChange(this);
		}
	}
	
	
	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadDataListener(OnLoadDataListener onLoadDataListener) {
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
