package com.codemobi.android.tvthailand.otv.model;

import com.codemobi.android.tvthailand.datasource.OnLoadDataListener;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTVEpisodes {
	private static final String EMPTY_STRING = "";

	private ArrayList<OTVEpisode> episodes = new ArrayList<>();
	
	public OTVEpisodes() {

	}

	public void loadOTVEpisodes(Program show) {
		notifyLoadStart();

		Call<JsonObject> call = APIClient.getClient().loadEpisodeOTV(show.getOtvId(), 0);
		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				clear();
				if (response.isSuccessful())
					jsonMap(response.body());
				notifyLoadFinish();
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				notifyLoadFinish();
			}
		});
	}

	protected void jsonMap(JsonObject jObject) {
		JsonArray jContentLists = jObject.get("contentList").getAsJsonArray();

		for (int i = 0; i < jContentLists.size(); i++) {
			try {
				JsonObject jContent = jContentLists.get(i).getAsJsonObject();

				ArrayList<OTVPart> parts = new ArrayList<>();
				JsonArray jArrayPart = jContent.get("item").getAsJsonArray();
				int count_part = jArrayPart.size();

				OTVPart part = null;
				for (int j = 0; j < count_part; j++) {
					JsonObject jObjPart = jArrayPart.get(j).getAsJsonObject();
					if (part == null) {
						part = new OTVPart();
					}

					if (jObjPart.has("media_code") && jObjPart.get("media_code").getAsString().equals("1001")) {
						part.setVastUrl(jObjPart.has("stream_url") ? jObjPart.get("stream_url").getAsString() : null);
					} else if (jObjPart.has("media_code")) {
						part.setPartId(jObjPart.has("id") ? jObjPart.get("id").getAsString() : EMPTY_STRING);
						part.setNameTh(jObjPart.has("name_th") ? jObjPart.get("name_th").getAsString() : EMPTY_STRING);
//						part.setNameEn(jObjPart.has("name_en") ? jObjPart.get("name_en").getAsString() : EMPTY_STRING);
						part.setThumbnail(jObjPart.has("thumbnail") ? jObjPart.get("thumbnail").getAsString() : EMPTY_STRING);
//						part.setCover(jObjPart.has("cover") ? jObjPart.get("cover").getAsString() : EMPTY_STRING);
						part.setStream_url(jObjPart.has("stream_url") ? jObjPart.get("stream_url").getAsString() : EMPTY_STRING);
						part.setMediaCode(jObjPart.get("media_code").getAsString());

						parts.add(part);
						part = null;
					}
				}

				insert(new OTVEpisode(jContent.has("episode_id") ? jContent.get("episode_id").getAsString() : EMPTY_STRING,
						jContent.has("name_th") ? jContent.get("name_th").getAsString() : EMPTY_STRING,
						jContent.has("detail") ? jContent.get("detail").getAsString() : EMPTY_STRING,
						jContent.has("thumbnail") ? jContent.get("thumbnail").getAsString() : EMPTY_STRING,
						jContent.has("cover") ? jContent.get("cover").getAsString() : EMPTY_STRING,
						jContent.has("date") ? jContent.get("date").getAsString() : EMPTY_STRING,
						parts));
			}
			catch (Exception e) {
				CrashlyticsCore.getInstance().logException(e);
			}
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
