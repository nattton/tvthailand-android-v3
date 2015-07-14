package com.codemobi.android.tvthailand.database.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.WhatsNewModel;

import android.os.AsyncTask;

public class WhatsNewInsertTask extends AsyncTask<JSONArray, Integer, Boolean> {
	private Dao<WhatsNewModel> mProgramDao;
	private int start = 0;

	public WhatsNewInsertTask(Dao<WhatsNewModel> programDao, int start) {
		this.mProgramDao = programDao;
		this.start = start;
	}

	@Override
	protected Boolean doInBackground(JSONArray... params) {
		if (start == 0) {
			mProgramDao.destroy();
		}
		JSONArray jArrayProgram = params[0];
		int length = jArrayProgram.length();
		for (int i = 0; i < length; i++) {
			try {
				WhatsNewModel program = new WhatsNewModel();

				JSONObject jObj = jArrayProgram.getJSONObject(i);

				program.setProgramId(jObj.getString("id"));
				program.setTitle(jObj.getString("title"));
				program.setThumbnail(jObj.getString("thumbnail"));
				program.setDescription(jObj.getString("description"));
				float rating = jObj.has("rating") ? jObj.getLong("rating") : 0;
				program.setRating(rating);
				program.setLastEpName(jObj.getString("last_epname"));

				mProgramDao.insert(program);
//				publishProgress(i + 1);
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
