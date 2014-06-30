package com.makathon.tvthailand.datasource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.contentprovider.EpisodeContentProvider;
import com.makathon.tvthailand.database.Dao;
import com.makathon.tvthailand.database.EpisodeModel;
import com.makathon.tvthailand.database.task.EpisodeDestroyInsertTask;
import com.makathon.tvthailand.database.task.EpisodeInsertTask;
import com.makathon.tvthailand.database.task.OnStateChangeListener;

public class Episodes {

	private String programId;
	private Program program;
	private ArrayList<Episode> episodes = new ArrayList<Episode>();
	private boolean loading = false;
	private boolean last = false;

	public interface OnLoadListener {
		void onLoadStart();

		void onLoadFinished();
	}

	private OnLoadListener onLoadListener;

	public void setOnLoadListener(OnLoadListener onLoadListener) {
		this.onLoadListener = onLoadListener;
	}
	
	public interface OnProgramChangeListener {
		void onProgramChange(Program program);
	}
	
	private OnProgramChangeListener onProgramChangeListener;
	
	public void setOnProgramChangeListener (Episodes.OnProgramChangeListener onProgramListener) {
		this.onProgramChangeListener = onProgramListener;
	}
	
	private void notifyProgramChange(JSONObject jObj) {
		if (this.onProgramChangeListener != null) {
			Program program = new Program(jObj);
			setProgram(program);
			this.onProgramChangeListener.onProgramChange(this.program);
		}
	}

	public interface OnEpisodeChangeListener {
		void onEpisodeChange(Episodes episodes);
	}

	private OnEpisodeChangeListener onEpisodeChangeListener;

	public void setOnEpisodeChangeListener(
			OnEpisodeChangeListener onEpisodeChangeListener) {
		this.onEpisodeChangeListener = onEpisodeChangeListener;
	}

	private Context mContext;
	private RequestQueue mRequestQueue;
	private int start = 0;
	
	public Episodes(Context context) {
		this.mContext = context;
		mRequestQueue = MyVolley.getRequestQueue();
	}

	public void jsonMap(JSONArray jArray) {
		int length = jArray.length();
		if(length == 0) last = true;
		for (int i = 0; i < length; i++) {
			try {
				JSONObject jObj = jArray.getJSONObject(i);
				insert(new Episode(mContext, jObj.getString("id"),
								jObj.getInt("ep"),		
								jObj.getString("title"),
								jObj.getString("video_encrypt"),
								jObj.getString("src_type"),
								jObj.getString("date"),
								jObj.getString("view_count"),
								jObj.getString("parts"),
								jObj.getString("pwd"))
						);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadEpisodes(String programId, int start) {
		loadEpisodes(programId, start, true);
	}
	
	public void loadEpisodes(String programId, int start, boolean shouldCache) {
		this.programId = programId;
		this.start = start;
		if (0 == start)
			last = false;
		if (loading || last)
			return;
		loading = true;
		
		notifyLoadStart();
		String url = String.format("%s/episode/%s/%d?device=android", AppUtility.BASE_URL, programId, start);
		JsonObjectRequest loadEpisodeRequest = new JsonObjectRequest(Method.GET, url, null, reqSuccessListener(), reqErrorListener());
		loadEpisodeRequest.setShouldCache(shouldCache);
		mRequestQueue.add(loadEpisodeRequest);
	}
	
	private Listener<JSONObject> reqSuccessListener() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				try {
					if (response.has("info")) {
						notifyProgramChange(response.getJSONObject("info"));
					}
					
					JSONArray jArray = response.getJSONArray("episodes");
					if (jArray.length() == 0) {
						last = true;
					}
					
					if (start == 0) {
						clear();
					}
					jsonMap(jArray);
					loading = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
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

	public void insert(Episode episode) {
		episodes.add(episode);
		notifyEpisodeChanged();
	}

	private void notifyLoadStart() {
		if (this.onLoadListener != null) {
			this.onLoadListener.onLoadStart();
		}
	}

	private void notifyLoadFinish() {
		if (this.onLoadListener != null) {
			this.onLoadListener.onLoadFinished();
		}
	}

	private void notifyEpisodeChanged() {
		if (this.onEpisodeChangeListener != null) {
			this.onEpisodeChangeListener.onEpisodeChange(this);
		}
	}

	public int size() {
		return episodes.size();
	}

	public Episode get(int position) {
		return episodes.get(position);
	}

	public void clear() {
		episodes.clear();
		notifyEpisodeChanged();
	}

	public Program getProgram() {
		return program;
	}

	public void setProgram(Program program) {
		this.program = program;
	}

}
