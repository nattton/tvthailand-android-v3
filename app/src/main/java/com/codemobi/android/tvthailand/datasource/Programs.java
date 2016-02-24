package com.codemobi.android.tvthailand.datasource;

import java.util.ArrayList;
import java.util.Map;

import com.codemobi.android.tvthailand.manager.http.APIClient;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Programs {
	private static final String EMPTY_STRING = "";
	private static final String TAG = "Programs";
	private ArrayList<Program> programs = new ArrayList<>();
	private boolean loading = false;
	private boolean last = false;

	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
	}

	public interface OnProgramChangeListener {
		void onProgramChange(Programs programs);
	}

	private OnProgramChangeListener onProgramChangeListener;

	public void setOnProgramChangeListener(
			OnProgramChangeListener onProgramChangeListener) {
		this.onProgramChangeListener = onProgramChangeListener;
	}

	public Programs() {

	}

	private int start = 0;

	public void jsonMap(JsonArray jArray) {
		int length = jArray.size();
		if (length == 0)
			last = true;
		for (int i = 0; i < length; i++) {
			JsonObject jObj = jArray.get(i).getAsJsonObject();
			String lastEpname = jObj.has("last_epname") ? jObj
					.get("last_epname").getAsString() : "";
			String id = jObj.has("id") ? jObj.get("id").getAsString() : EMPTY_STRING;
			String title = jObj.has("title") ? jObj.get("title").getAsString() : EMPTY_STRING;
			String thumbnail = jObj.has("thumbnail") ? jObj.get("thumbnail").getAsString() : EMPTY_STRING;
			String description = jObj.has("description") ? jObj.get("description").getAsString() : EMPTY_STRING;
			int isOTV = jObj.has("is_otv") ? jObj.get("is_otv").getAsInt() : 0;
			String otvId = jObj.has("otv_id") ? jObj.get("otv_id").getAsString() : EMPTY_STRING;
			String otvLogo = jObj.has("otv_logo") ? jObj.get("otv_logo").getAsString() : EMPTY_STRING;
			insert(new Program(id, title, thumbnail, description, lastEpname, isOTV, otvId, otvLogo));
		}
	}

	private Callback<JsonObject> jsonObjectCallback() {
		return new Callback<JsonObject>() {
			@Override
			public void onResponse(Response<JsonObject> response) {
				if (response.isSuccess()) {
					if (0 == start) {
						clear();
					}
					try {
						JsonArray programArr = response.body().getAsJsonArray("programs");
						jsonMap(programArr);
					} catch (Exception e) {
						e.printStackTrace();
					}
					loading = false;
				}
				notifyLoadFinish();
			}

			@Override
			public void onFailure(Throwable t) {
				notifyLoadFinish();
				notifyLoadError("Cannot load data.");
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
		Call<JsonObject> call = APIClient.getClient().loadProgramByCategory(id, start, Constant.defaultParams);
		call.enqueue(jsonObjectCallback());
	}

	public void loadProgramByChannel(String id, int start) {
		this.start = start;
		if (0 == start)
			last = false;
		if (loading || last)
			return;
		loading = true;

		notifyLoadStart();
		Call<JsonObject> call = APIClient.getClient().loadProgramByChannel(id, start, Constant.defaultParams);
		call.enqueue(jsonObjectCallback());
	}

	public void loadProgramBySearch(String keyword, int start) {
		this.start = start;
		if (0 == start)
			last = false;
		if (loading || last)
			return;
		loading = false;

		notifyLoadStart();
		Map<String, String> params = Constant.defaultParams;
		params.put("keyword", keyword);
		Call<JsonObject> call = APIClient.getClient().loadProgramBySearch(start, params);
		call.enqueue(jsonObjectCallback());
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

	private void notifyLoadError(String error) {
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadError(error);
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
