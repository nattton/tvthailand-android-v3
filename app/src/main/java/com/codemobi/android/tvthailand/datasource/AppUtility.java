package com.codemobi.android.tvthailand.datasource;

import com.codemobi.android.tvthailand.utils.Constant;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

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

	public static boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static String getCurrentTime() {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMDDHHmm");
		return sdf.format(c.getTime());
	}
}
