package com.codemobi.android.tvthailand.manager.http;

import android.util.Log;

import com.codemobi.android.tvthailand.BuildConfig;
import com.codemobi.android.tvthailand.dao.advertise.AdCollectionDao;
import com.codemobi.android.tvthailand.dao.advertise.PreRollAdCollectionDao;
import com.codemobi.android.tvthailand.dao.section.SectionCollectionDao;
import com.codemobi.android.tvthailand.otv.OTVConfig;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.gson.JsonObject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.Map;

import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * API Client with Retrofit
 * http://square.github.io/retrofit/
 * https://github.com/ayon115/Retrofit2.0Sample
 * http://inthecheesefactory.com/blog/retrofit-2.0/th
 * Created by nattapong on 10/10/2015 AD.
 */

public class APIClient {
    private static APIService service;
    public static final String API_URL = Constant.BASE_URL;

    public interface APIService {
        @GET("advertise")
        Call<AdCollectionDao> loadAd(@QueryMap Map<String, String> query);

        @GET("preroll_advertise")
        Call<PreRollAdCollectionDao> loadPreRollAd(@QueryMap Map<String, String> query);

        @GET("section")
        Call<SectionCollectionDao> loadSection(@QueryMap Map<String, String> query);

        @GET("category/{id}/{start}")
        Call<JsonObject> loadProgramByCategory(@Path("id") String id, @Path("start") int start, @QueryMap Map<String, String> query);

        @GET("channel/{id}/{start}")
        Call<JsonObject> loadProgramByChannel(@Path("id") String id, @Path("start") int start, @QueryMap Map<String, String> query);

        @GET("search/{start}")
        Call<JsonObject> loadProgramBySearch(@Path("start") int start, @QueryMap Map<String, String> query); // ?keyword

        @GET("episode/{id}/{start}")
        Call<JsonObject> loadEpisodeByProgram(@Path("id") String id, @Path("start") int start, @QueryMap Map<String, String> query);

        @GET(OTVConfig.BASE_URL + "/Content/index/" + OTVConfig.APP_ID + "/" + BuildConfig.VERSION_NAME + "/" + OTVConfig.API_VERSION + "/{id}/{start}/50/0")
        Call<JsonObject> loadEpisodeOTV(@Path("id") String id, @Path("start") int start);

    }

    public static synchronized APIService getClient() {
        if (service == null) {
            OkHttpClient okClient;
            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                okClient = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .build();
            } else {
                okClient = new OkHttpClient.Builder().build();
            }

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(APIService.class);
        }
        return service;
    }

    public static String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}