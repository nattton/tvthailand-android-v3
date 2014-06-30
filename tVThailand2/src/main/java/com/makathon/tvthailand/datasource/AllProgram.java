package com.makathon.tvthailand.datasource;

import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.makathon.tvthailand.contentprovider.ProgramContentProvider;
import com.makathon.tvthailand.database.Dao;
import com.makathon.tvthailand.database.ProgramModel;
import com.makathon.tvthailand.database.task.ProgramDestroyInsertTask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.util.Log;

public class AllProgram extends Application {
	private static AllProgram instance;

	public AllProgram(Context context) {
		this.mContext = context;
	}

	public static synchronized AllProgram getInstance(Context context) {
		if (instance == null) {
			instance = new AllProgram(context);
		}
		return instance;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	String TAG = "AllProgram";

	private static final String PREF_ALLPROGRAM_TIME = "PREF_ALLPROGRAM_TIME";
	private static final long ALLPROGRAM_TIME = 1800000l;

	private Context mContext;
	ProgramDestroyInsertTask mTask;

	private OnLoadDataListener onLoadDataListener;

	public void setOnLoadDataListener(OnLoadDataListener onLoadDataListener) {
		this.onLoadDataListener = onLoadDataListener;
	}

	public void load() {
		load(false);
	}

	public void load(boolean isForce) {
		if (mTask != null && mTask.getStatus() == Status.RUNNING)
			return;

		SharedPreferences prefs = mContext.getSharedPreferences(AppUtility.PREF_NAME,
				Context.MODE_PRIVATE);
		long time = prefs.getLong(PREF_ALLPROGRAM_TIME, 0);
		Date nextUpdateTime = new Date(time + ALLPROGRAM_TIME);
		Date currentTime = new Date();
//		Log.e("loadAllProgram", "currentTime : " + currentTime);
//		Log.e("loadAllProgram", "nextUpdateTime : " + nextUpdateTime);
		if (isForce) {
//			Log.e("loadAllProgram", "Start Load All Program");
			AppUtility.getInstance().allProgram(jsonHandler);
		} else if (currentTime.after(nextUpdateTime)) {
//			Log.e("loadAllProgram", "Pre Load Program");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
//					Log.e("loadAllProgram", "Start Load All Program");
					AppUtility.getInstance().allProgram(jsonHandler);
				}
			}, 10000);
		}
	}

	// public void loadAllProgramDB() {
	// String sortOrder = ProgramColumns.TITLE + " COLLATE LOCALIZED ASC";
	// Dao<ProgramModel> mProgramDao = new Dao<ProgramModel>(
	// ProgramModel.class, mContext,
	// ProgramContentProvider.CONTENT_URI, null, sortOrder);
	// List<ProgramModel> mProgramAll = mProgramDao.get(null, null);
	// for (ProgramModel programModel : mProgramAll) {
	// insertNotNotify(new Program(programModel.getProgramId(),
	// programModel.getTitle(), programModel.getThumbnail(),
	// programModel.getDescription(), programModel.getRating()));
	// }
	// notifyProgramsChanged();
	// }

	private JsonHttpResponseHandler jsonHandler = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(JSONObject programObjs) {
			try {
				JSONArray programArr = programObjs.getJSONArray("programs");

				Dao<ProgramModel> mProgramDao = new Dao<ProgramModel>(
						ProgramModel.class, mContext,
						ProgramContentProvider.CONTENT_URI);
				mTask = new ProgramDestroyInsertTask(mProgramDao) {

					@Override
					protected void onPostExecute(Boolean result) {
						notifyLoadFinished();
						if (result) {
							SharedPreferences prefs = mContext
									.getSharedPreferences(AppUtility.PREF_NAME,
											Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = prefs.edit();
							editor.putLong(PREF_ALLPROGRAM_TIME,
									new Date().getTime());
							editor.commit();

//							Log.e("loadAllProgram",
//									"Finish Start Load All Program");
						}
						super.onPostExecute(result);
					}

					@Override
					protected void onProgressUpdate(Integer... values) {
						super.onProgressUpdate(values);
					}

				};
				mTask.execute(programArr);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStart() {
			notifyLoadStart();
		}

		@Override
		public void onFailure(Throwable thr) {
			Log.e(TAG, "Failure Load AllProgram");
			notifyLoadFinished();
		}
	};

	public void notifyLoadStart() {
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadStart();
		}
	}

	public void notifyLoadFinished() {
		if (this.onLoadDataListener != null) {
			this.onLoadDataListener.onLoadFinished();
		}
	}
}
