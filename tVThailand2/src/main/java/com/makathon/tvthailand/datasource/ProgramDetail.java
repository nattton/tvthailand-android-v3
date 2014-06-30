package com.makathon.tvthailand.datasource;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

public class ProgramDetail {
	
	private boolean loading = false; 
	
	/**
	 * property
	 */
	private String id;
	private String title;
	private String description;
	private String thumbnail;
	private String detail;
	private String viewCount;
	private float rating;
	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getDetail() {
		return detail;
	}

	public String getViewCount() {
		return viewCount;
	}
	
	public float getRating() {
		return rating;
	}
	
	public interface OnProgramDetailChangeListener {
		void onProgramDetailChange(ProgramDetail programDetail);
	}
	
	Context mContext;
	public ProgramDetail(Context context) {
		mContext = context;
	}
	
	private OnProgramDetailChangeListener onProgramDetailChangeListener;
	
	public void setOnProgramDetailChangeListener(OnProgramDetailChangeListener onProgramDetailChangeListener) {
		this.onProgramDetailChangeListener = onProgramDetailChangeListener;
	}
	
	public void loadProgramDetail(String id) {
		Log.d("TEST", "loadProgramDetail Start");
		if (loading) return;
		loading = true;
		AppUtility.getInstance().programInfo(id, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject programObjs) {
				jsonMap(programObjs);
			}
			
			@Override
			public void onFinish() {
				loading = false;
//				Log.d("TEST", "loadWhatsNewProgram onFinish");
			}
		});
	}
	
	public void jsonMap(JSONObject jObj) {
		try {
			this.id = jObj.getString("id");
			this.title = jObj.getString("title");
			this.description = jObj.getString("description");
			this.thumbnail = jObj.getString("thumbnail");
			this.detail = jObj.getString("detail");
			this.viewCount = jObj.getString("view_count");
			this.rating = (float)jObj.getLong("rating");
			this.
			notifyProgramDetailChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void notifyProgramDetailChanged() {
		if(this.onProgramDetailChangeListener != null) {
			this.onProgramDetailChangeListener.onProgramDetailChange(this);
		}
	}
	
}
