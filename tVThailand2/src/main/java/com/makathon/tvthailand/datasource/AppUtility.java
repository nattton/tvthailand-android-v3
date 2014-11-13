package com.makathon.tvthailand.datasource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makathon.tvthailand.otv.datasoruce.OTVEpisode;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.format.Time;

@SuppressLint("DefaultLocale")
public class AppUtility extends Application {

	private static AppUtility instance = new AppUtility();

	private AppUtility() {
	}

	public static synchronized AppUtility getInstance() {
		return instance;
	}

	private static OTVEpisode episodeSelected = null;

	public static OTVEpisode getEpisodeSelected() {
		return episodeSelected;
	}

	public static void setEpisodeSelected(OTVEpisode episodeSelected) {
		AppUtility.episodeSelected = episodeSelected;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	public static final String PREFS_LOGIN = "PREFLOGIN";
	public static final String PREFS_LOGIN_ISLOGIN = "ISLOGIN";
	public static final String PREF_NAME = "TVThailand";
	public static final String ADMOB_ID = "aa049b7e364e4722";
	public static final String DEVELOPER_KEY = "AIzaSyAecHtNarrTvvwlb-OjS-wRlqCRFuRUT0o";
	public static final String BASE_URL = "http://tv.makathon.com/api2";

    public static final String UserAgentChrome = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36 ";
	public static final String UserAgentTablet = "Mozilla/5.0 (iPad; CPU OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D201 Safari/9537.53";
	public static final String UserAgentMobile = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D201 Safari/9537.53";

	public static final List<String> PERMISSIONS = Arrays.asList(
			"user_birthday", "email", "user_location");

	private AsyncHttpClient client = new AsyncHttpClient();

	private Section sections = null;

	public Section getSections(Context context) {
		if (sections == null) {
			sections = new Section(context);
		}
		return sections;
	}

	private Categories categories = null;

	public Categories getCategories(Context context) {
		if (categories == null) {
			categories = new Categories(context);
		}
		return categories;
	}

	private Channels channels = null;

	public Channels getChannels(Context context) {
		if (channels == null) {
			channels = new Channels(context);
		}
		return channels;
	}

	private Radios radios = null;

	public Radios getRadios(Context context) {
		if (radios == null) {
			radios = new Radios(context);
		}
		return radios;
	}

	private Programs topHitsPrograms = null;

	public Programs getTopHitsPrograms(Context context) {
		if (topHitsPrograms == null) {
			topHitsPrograms = new Programs(context);
		}
		return topHitsPrograms;
	}

    public static String getUserAgentChrome() {
        return UserAgentChrome;
    }

	public static String getUserAgentiOS(Context activityContext) {
		return isTablet(activityContext) ? UserAgentTablet : UserAgentMobile;
	}

	/**
	 * Checks if the device is a tablet or a phone
	 * 
	 * @param context
	 *            The Activity Context.
	 * @return Returns true if the device is a Tablet
	 */
	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	static final Time today = new Time(Time.getCurrentTimezone());

	public static String getCurrentTime() {
		today.setToNow();
		return today.format("%Y%m%d%H%M");
	}

	public void tophits(int start, JsonHttpResponseHandler responseHandler) {
		String url = String.format("%s/tophits/%d?device=android&lr=1",
				BASE_URL, start);
		client.get(url, responseHandler);
	}

	public void allProgram(JsonHttpResponseHandler responseHandler) {
		String url = String.format("%s/all_program?device=android&lr=1",
				BASE_URL);
		client.get(url, responseHandler);
	}

	public void episode(String programId, int start,
			JsonHttpResponseHandler responseHandler) {
		String url = String.format(
				"%s/episode/%s/%d?device=android&lr=1&time=%s", BASE_URL,
				programId, start, getCurrentTime());
		client.get(url, responseHandler);
	}

	public void programInfo(String programId,
			JsonHttpResponseHandler responseHandler) {
		String url = String.format("%s/program_nfo/%s?device=android",
				BASE_URL, programId);
		client.get(url, responseHandler);
	}

	public void viewEpisode(String programId,
			AsyncHttpResponseHandler responseHandler) {
		String url = String.format("%s/view_episode/%s?device=android",
				BASE_URL, programId);
		client.get(url, responseHandler);
	}
}
