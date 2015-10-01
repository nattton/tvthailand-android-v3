package com.codemobi.android.tvthailand.database.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.ProgramModel;

import android.os.AsyncTask;

public class ProgramDestroyInsertTask extends
		AsyncTask<JSONArray, Integer, Boolean> {
	private Dao<ProgramModel> mProgramDao;
	private boolean shouldStop = false;

	public ProgramDestroyInsertTask(Dao<ProgramModel> programDao) {
		this.mProgramDao = programDao;
	}

	@Override
	protected Boolean doInBackground(JSONArray... params) {
		JSONArray jArrayProgram = params[0];
		mProgramDao.destroy();
		int length = jArrayProgram.length();
		for (int i = 0; i < length; i++) {
			try {
				ProgramModel program = new ProgramModel();

				JSONObject jObj = jArrayProgram.getJSONObject(i);

				program.setProgramId(jObj.getString("id"));
				program.setTitle(jObj.getString("title"));
				program.setThumbnail(jObj.getString("thumbnail"));
				program.setDescription(jObj.getString("description"));
				float rating = jObj.has("rating") ? jObj.getLong("rating") : 0;
				program.setRating(rating);

				mProgramDao.insert(program);
//				Log.i("ProgramTask Insert", jObj.getString("title"));
				// publishProgress(i + 1);
				if (shouldStop) {
					return false;
				}
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

	@Override
	protected void onCancelled() {
		shouldStop = true;
		super.onCancelled();
	}

}
