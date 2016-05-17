package com.codemobi.android.tvthailand.datasource;

import java.util.ArrayList;

import android.content.Context;

import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
	
	private void notifyProgramChange(JsonObject jObj) {
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
	private int start = 0;
	
	public Episodes(Context context) {
		this.mContext = context;
	}

	public void jsonMap(JsonArray jArray) {
		int length = jArray.size();
		if(length == 0) last = true;
		for (int i = 0; i < length; i++) {
			JsonObject jObj = jArray.get(i).getAsJsonObject();
			insert(new Episode(mContext, jObj.get("id").getAsString(),
							jObj.get("ep").getAsInt(),
							jObj.get("title").getAsString(),
							jObj.get("video_encrypt").getAsString(),
							jObj.get("src_type").getAsString(),
							jObj.get("date").getAsString(),
							jObj.get("view_count").getAsString(),
							jObj.get("parts").getAsString(),
							jObj.get("pwd").getAsString())
			);
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
		Call<JsonObject> call = APIClient.getClient().loadEpisodeByProgram(this.programId, start, Constant.defaultParams);
		call.enqueue(jsonObjectCallback());
	}

	private Callback<JsonObject> jsonObjectCallback() {

		return new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				if (response.isSuccessful()) {
					if (response.body().has("info")) {
						notifyProgramChange(response.body().getAsJsonObject("info"));
					}
					JsonArray jArray = response.body().get("episodes").getAsJsonArray();
					if (jArray.size() == 0) {
						last = true;
					}

					if (start == 0) {
						clear();
					}
					jsonMap(jArray);
					loading = false;
				}
				notifyLoadFinish();
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
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
