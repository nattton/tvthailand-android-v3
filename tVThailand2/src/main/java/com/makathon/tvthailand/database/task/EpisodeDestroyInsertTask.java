package com.makathon.tvthailand.database.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.makathon.tvthailand.database.Dao;
import com.makathon.tvthailand.database.EpisodeModel;
import com.makathon.tvthailand.database.EpisodeTable;

import android.content.Context;
import android.os.AsyncTask;

public class EpisodeDestroyInsertTask extends AsyncTask<String, Integer, Void> {
	private Context context;
	private Dao<EpisodeModel> mEpisodeDao;
	private String progamId;
	private JSONArray jArray;
	private boolean shouldStop;
	
	private OnStateChangeListener onStateChangeListener;
	
	public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
		this.onStateChangeListener = onStateChangeListener;
	}
	
	private void notifyFinish() {
		if (onStateChangeListener != null) {
			onStateChangeListener.onFinish();
		}
	}

	public EpisodeDestroyInsertTask(Context context,
			Dao<EpisodeModel> episodeDao, String progamId, JSONArray jArray) {
		this.context = context;
		this.mEpisodeDao = episodeDao;
		this.progamId = progamId;
		this.jArray = jArray;
	}

	@Override
	protected Void doInBackground(String... params) {
		String selection = EpisodeTable.EpisodeColumns.PROGRAM_ID + " = "
				+ progamId;
		for (EpisodeModel epidose : mEpisodeDao.get(selection, null)) {
			mEpisodeDao.delete(epidose);
		}
		
		int length = jArray.length();
		for (int i = 0; i < length; i++) {
			try {
				JSONObject jObj = jArray.getJSONObject(i);
				EpisodeModel episode = new EpisodeModel(context, progamId,
						jObj.getString("id"), jObj.getInt("ep"),
						jObj.getString("title"),
						jObj.getString("video_encrypt"),
						jObj.getString("src_type"), jObj.getString("date"),
						jObj.getString("view_count"), jObj.getString("parts"),
						jObj.getString("pwd"));
				mEpisodeDao.insert(episode);
//				publishProgress(i + 1);
				if (shouldStop) {
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		notifyFinish();
		return null;
	}
	
	@Override
	protected void onCancelled() {
		shouldStop = true;
		super.onCancelled();
	}

}
