package com.codemobi.android.tvthailand.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nattapong on 12/12/14 AD.
 */
public class Constant {

    public static final String DEVELOPER_KEY = "AIzaSyAecHtNarrTvvwlb-OjS-wRlqCRFuRUT0o";
    public static final String BASE_URL = "http://tv.makathon.com/api3";

    public static final String UserAgentChrome = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36 ";
    public static final String UserAgentTablet = "Mozilla/5.0 (iPad; CPU OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D201 Safari/9537.53";
    public static final String UserAgentMobile = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Version/7.0 Mobile/11D201 Safari/9537.53";

    public static final List<String> PERMISSIONS = Arrays.asList("user_birthday", "email", "user_location");

    public static final String PREF_NAME = "TV_THAILAND";
    public static final String PREF_SECTION = "SECTION";
    public static Map<String, List<String>> getDefaultParams() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("device", Arrays.asList("android"));
        params.put("app_version", Arrays.asList(Utils.getInstance().getVersionName()));
        return Collections.unmodifiableMap(params);
    }
}
