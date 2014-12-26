package com.makathon.tvthailand.datasource;

import com.makathon.tvthailand.utils.Constant;
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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Clone is not allowed.");
	}

	public static final String PREFS_LOGIN = "PREFLOGIN";
	public static final String PREFS_LOGIN_ISLOGIN = "ISLOGIN";
	public static final String PREF_NAME = "TVThailand";

    public static String getUserAgentChrome() {
        return Constant.UserAgentChrome;
    }

	public static String getUserAgentiOS(Context activityContext) {
		return isTablet(activityContext) ? Constant.UserAgentTablet : Constant.UserAgentMobile;
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
}
