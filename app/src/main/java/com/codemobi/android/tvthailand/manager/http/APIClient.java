package com.codemobi.android.tvthailand.manager.http;

import android.util.Log;

import com.codemobi.android.tvthailand.BuildConfig;
import com.codemobi.android.tvthailand.dao.advertise.AdCollectionDao;
import com.codemobi.android.tvthailand.dao.advertise.PreRollAdCollectionDao;
import com.codemobi.android.tvthailand.dao.section.SectionCollectionDao;
import com.codemobi.android.tvthailand.otv.OTVConfig;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.util.Map;

import okio.Buffer;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.Call;
import retrofit.http.Path;
import retrofit.http.QueryMap;

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
            OkHttpClient okClient = new OkHttpClient();
            okClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Response response = chain.proceed(chain.request());
                    return response;
                }
            });

            if (BuildConfig.DEBUG) {
                okClient.interceptors().add(new LoggingInterceptor());
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

    public static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            String requestLog = String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers());
            if(request.method().compareToIgnoreCase("post")==0){
                requestLog ="\n"+requestLog+"\n"+bodyToString(request);
            }
            Log.d("RETROFIT", "request" + "\n" + requestLog);

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();

            String responseLog = String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers());

            String bodyString = response.body().string();

            Log.d("RETROFIT","response"+"\n"+responseLog+"\n"+bodyString);

            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        }
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