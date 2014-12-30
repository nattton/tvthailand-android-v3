package com.makathon.tvthailand.datasource;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.makathon.tvthailand.MyVolley;
import com.makathon.tvthailand.dao.show.ShowCollectionDao;
import com.makathon.tvthailand.manager.http.GsonRequest;
import com.makathon.tvthailand.utils.Constant;

public class Programs {
	private static final String EMPTY_STRING = "";
	private static final String TAG = "Programs";
	private RequestQueue mRequestQueue;
	private ArrayList<Program> programs = new ArrayList<>();
	private boolean loading = false;
	private boolean last = false;

	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
	}

	public interface OnProgramChangeListener {
		void onProgramChange(Programs Programs);
	}

	private OnProgramChangeListener onProgramChangeListener;

	public void setOnProgramChangeListener(
			OnProgramChangeListener onProgramChangeListener) {
		this.onProgramChangeListener = onProgramChangeListener;
	}

	public Programs() {
		mRequestQueue = MyVolley.getRequestQueue();
	}

	private int start = 0;

	public void jsonMap(JSONArray jArray) {
		int length = jArray.length();
		if (length == 0)
			last = true;
		for (int i = 0; i < length; i++) {
			try {
				JSONObject jObj = jArray.getJSONObject(i);
				String lastEpname = jObj.has("last_epname") ? jObj
						.getString("last_epname") : "";
				float rating = jObj.has("rating") ? jObj.getLong("rating") : 0;
				String id = jObj.has("id") ? jObj.getString("id") : EMPTY_STRING;
				String title = jObj.has("title") ? jObj.getString("title") : EMPTY_STRING;
				String thumbnail = jObj.has("thumbnail") ? jObj.getString("thumbnail") : EMPTY_STRING;
				String description = jObj.has("description") ? jObj.getString("description") : EMPTY_STRING;
				int isOTV = jObj.has("is_otv") ? jObj.getInt("is_otv") : 0;
				String otvId = jObj.has("otv_id") ? jObj.getString("otv_id") : EMPTY_STRING;
				String otvApiName = jObj.has("otv_api_name") ? jObj.getString("otv_api_name") : EMPTY_STRING;
				insert(new Program(id, title, thumbnail, description, rating, lastEpname, isOTV, otvId, otvApiName));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Response.Listener<JSONObject> reqSuccessListenner() {
		return new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				if (0 == start) {
					clear();
				}
				try {
					JSONArray programArr = response.getJSONArray("programs");
					jsonMap(programArr);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				notifyLoadFinish();
				loading = false;
			}
			
		};
	}
	
	private Response.ErrorListener reqErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		};
	}
	
	public void loadProgramByCategory(String id, int start) {
		this.start = start;
		if (0 == start)
			last = false;
		if (loading || last)
			return;
		loading = true;
		
		notifyLoadStart();
		String url = String.format("%s/category/%s/%d?device=android&time=%s", Constant.BASE_URL, id, start, AppUtility.getCurrentTime());
		JsonObjectRequest loadProgramRequest = new JsonObjectRequest(Method.GET, url, null,
				reqSuccessListenner(), reqErrorListener());
		mRequestQueue.add(loadProgramRequest);

//        mRequestQueue.add(new GsonRequest<ShowCollectionDao>(url, ShowCollectionDao.class, null, new Response.Listener<ShowCollectionDao>() {
//            @Override
//            public void onResponse(ShowCollectionDao response) {
//                Log.d("ShowCollectionDao", response.getShows().get(0).getTitle());
//            }
//        }, reqErrorListener()));
	}

	public void loadProgramByChannel(String id, int start) {
		this.start = start;
		if (0 == start)
			last = false;
		if (loading || last)
			return;
		loading = true;
		
		notifyLoadStart();
		String url = String.format("%s/channel/%s/%d?device=android&time=%s", Constant.BASE_URL, id, start, AppUtility.getCurrentTime());
		JsonObjectRequest loadProgramRequest = new JsonObjectRequest(Method.GET, url, null, 
				reqSuccessListenner(), reqErrorListener());
		mRequestQueue.add(loadProgramRequest);
	}

	public void loadProgramBySearch(String keyword, int start) {
		this.start = start;
		if (0 == start)
			last = false;
		if (loading || last)
			return;
		loading = false;
		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		
		notifyLoadStart();
		String url = String.format("%s/search/%d?&keyword=%s&device=android&time=%s",
                Constant.BASE_URL, start, keyword, AppUtility.getCurrentTime());
		JsonObjectRequest loadProgramRequest = new JsonObjectRequest(Method.GET, url, null, 
				reqSuccessListenner(), reqErrorListener());
		mRequestQueue.add(loadProgramRequest);
	}

	public void insert(Program program) {
		programs.add(program);
		notifyProgramsChanged();
	}

	public void insertNotNotify(Program program) {
		programs.add(program);
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

	private void notifyProgramsChanged() {
		if (this.onProgramChangeListener != null) {
			this.onProgramChangeListener.onProgramChange(this);
		}
	}

	public int size() {
		return programs.size();
	}

	public Program get(int position) {
		return programs.get(position);
	}

	public void clear() {
		programs.clear();
		notifyProgramsChanged();
	}
}
