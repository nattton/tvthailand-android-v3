package com.codemobi.android.tvthailand.datasource;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codemobi.android.tvthailand.MyVolley;
import com.codemobi.android.tvthailand.utils.Constant;

import io.vov.vitamio.utils.Log;

public class Episodes {

	private String programId;
	private Program program;
	private ArrayList<Episode> episodes = new ArrayList<>();
	private boolean loading = false;
	private boolean last = false;

	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadDataListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
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
		mRequestQueue = MyVolley.getInstance(context).getRequestQueue();
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
		String url = String.format("%s/episode/%s/%d?device=android&time=%s", Constant.BASE_URL, this.programId, start, AppUtility.getCurrentTime());
		JsonObjectRequest loadEpisodeRequest = new JsonObjectRequest(Request.Method.GET, url, reqSuccessListener(), reqErrorListener());
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
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadStart();
		}
	}

	private void notifyLoadFinish() {
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadFinished();
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
