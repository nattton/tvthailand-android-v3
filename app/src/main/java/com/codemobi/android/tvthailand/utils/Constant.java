package com.codemobi.android.tvthailand.utils;

import com.codemobi.android.tvthailand.BuildConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Constant {

    public static final String BASE_URL = BuildConfig.BASE_URL;

    public static final String DEVELOPER_KEY = "AIzaSyAecHtNarrTvvwlb-OjS-wRlqCRFuRUT0o";

    public static String UserAgentChrome = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    public static final String UserAgentTablet = "Mozilla/5.0 (iPad; CPU OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D201 Safari/9537.53";
    public static final String UserAgentMobile = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D201 Safari/9537.53";

    public static final List<String> PERMISSIONS = Arrays.asList("user_birthday", "email", "user_location");

    public static final String PREF_NAME = "TV_THAILAND";
    public static final String PREF_SECTION =  "SECTION";

    public static final HashMap<String, String> defaultParams = new HashMap<>();
    static {
        defaultParams.put("device", "android");
        defaultParams.put("appId", String.valueOf(BuildConfig.APPLICATION_ID));
        defaultParams.put("version", BuildConfig.VERSION_NAME);
        defaultParams.put("build", String.valueOf(BuildConfig.VERSION_CODE));
    }
}
