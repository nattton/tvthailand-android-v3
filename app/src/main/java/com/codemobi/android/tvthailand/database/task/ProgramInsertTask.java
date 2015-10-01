package com.codemobi.android.tvthailand.database.task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codemobi.android.tvthailand.database.Dao;
import com.codemobi.android.tvthailand.database.ProgramModel;


import android.os.AsyncTask;

public class ProgramInsertTask extends AsyncTask<String, Integer, Void> {
	private Dao<ProgramModel> mProgramDao;
	private JSONArray jArrayProgram;
	private boolean shouldStop;
	
	public ProgramInsertTask(Dao<ProgramModel> programDao, JSONArray jArray) {
		this.mProgramDao = programDao;
		this.jArrayProgram = jArray;
	}
	
	@Override
	protected Void doInBackground(String... params) {
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
//				publishProgress(i + 1);
				if (shouldStop) {
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	protected void onCancelled() {
		shouldStop = true;
		super.onCancelled();
	}

}
