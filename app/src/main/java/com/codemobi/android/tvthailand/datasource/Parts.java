package com.codemobi.android.tvthailand.datasource;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.codemobi.android.tvthailand.BuildConfig;
import com.codemobi.android.tvthailand.utils.Constant;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.codemobi.android.tvthailand.R;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class Parts {
	private ArrayList<Part> parts = new ArrayList<>();
	private String title;
	private String[] videos;
	private String srcType;
	private String password;

	private String display;

	private String selectedVideoId;

	public interface OnLoadListener {
		void onStart();

		void onFinish();

        void onError(String message);
	}

    private OnLoadListener onLoadListener;

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    private void notifyStart() {
        if (this.onLoadListener != null) this.onLoadListener.onStart();
    }

    private void notifyFinish() {
        if (this.onLoadListener != null) this.onLoadListener.onFinish();
    }

    private void notifyError(String message) {
        if (this.onLoadListener != null) this.onLoadListener.onError(message);
    }

	public interface OnEpisodeChangeListener {
		void onEpisodeChange(Parts episodes);
	}

	private OnEpisodeChangeListener onEpisodeChangeListener;

	public void setOnProgramChangeListener(
			OnEpisodeChangeListener onEpisodeChangeListener) {
		this.onEpisodeChangeListener = onEpisodeChangeListener;
	}

    private Activity mActivity;
	private Context mContext;

	public Parts(Context mContext, String title, String icon, String[] videos,
			String srcType, String password) {
        this.mContext = mContext;
		setEpisode(title, icon, videos, srcType, password);
	}

	public void setEpisode(String title, String icon, String[] videos,
			String srcType, String password) {
		this.title = title;
		this.videos = videos;
		this.srcType = srcType;
		this.password = password;
		clear();
		for (int i = 0; i < videos.length; i++) {
			insert(new Part(i + 1, videos[i], srcType));
		}
	}

	public void insert(Part part) {
		parts.add(part);
		notifyEpisodeChanged();
	}

	private void notifyEpisodeChanged() {
		if (this.onEpisodeChangeListener != null) {
			this.onEpisodeChangeListener.onEpisodeChange(this);
		}
	}

	public int size() {
		return parts.size();
	}

	public Part get(int position) {
		return parts.get(position);
	}

	public void clear() {
		parts.clear();
		notifyEpisodeChanged();
	}

	public void playVideoPart(int position) {
		if (videos.length == 0) {
			display = title;
		} else {
			display = String.format("%s - Part %d", title, position + 1);
		}

		String videoId = videos[position];
		if (videoId.equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("Cannot play this video.");
			builder.setNegativeButton(R.string.ok, null);
			AlertDialog dialog = builder.create();
			dialog.show();
			return;
		}

		switch (srcType) {
			case "0":
				if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(mContext) == YouTubeInitializationResult.SUCCESS) {
					if(YouTubeIntents.isYouTubeInstalled(mContext)) {
						Intent intent = YouTubeIntents.createPlayVideoIntent(mContext, videoId);
						mContext.startActivity(intent);
					} else {
						mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
								"http://www.youtube.com/watch?v=%s", videoId))));
					}
				} else {
					if (YouTubeIntents.isYouTubeInstalled(mContext)) {
						Intent intent = YouTubeIntents.createPlayVideoIntent(
								mContext, videos[position]);
						mContext.startActivity(intent);
					} else {
						mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse(String.format(
										"http://www.youtube.com/watch?v=%s",
										videos[position]))));
					}
				}
				break;
			case "1":
				openWithDailyMotion(videoId);
				break;
			case "11":
				Uri uriVideo = Uri.parse(videoId);
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, uriVideo));
				break;
			case "12":
				playVideo(videoId);
				break;
			case "13":
			case "14":
				loadMThaiVideoFromWeb(videoId);
				break;
			case "15":
				loadMThaiVideoWithPassword(videoId, password);
				break;
			default:
				break;
		}
	}

	private boolean dailymotionInstall() {
		PackageManager pm = mContext.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo("com.dailymotion.dailymotion",
					PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}

	private void openWithDailyMotion(String videoId) {
		final Uri uriVideo = Uri.parse("http://www.dailymotion.com/video/"
				+ videoId);
		if (dailymotionInstall()) {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, uriVideo));
		} else {
			AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
			alert.setTitle("Recommended Dailymotion Player");
			alert.setMessage("Install Apps Dailymotion for play this video.\nInstall Now?");
			alert.setPositiveButton(R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Uri uriApps = Uri
							.parse("https://play.google.com/store/apps/details?id=com.dailymotion.dailymotion");
					mContext.startActivity(new Intent(Intent.ACTION_VIEW,
							uriApps));
				}
			});
			alert.setNegativeButton("Later", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mContext.startActivity(new Intent(Intent.ACTION_VIEW,
							uriVideo));
				}
			});
			alert.show();
		}

	}

	private void startViedo(String videoUrl) {
		Intent intentVideo = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
		intentVideo.putExtra(Intent.EXTRA_TITLE, display);
		intentVideo.setDataAndType(Uri.parse(videoUrl), "video/*");
		mContext.startActivity(intentVideo);
	}

	private void playVideo(String path) {
		startViedo(path);
	}


    private void openMThaiVideo(final String videoId) {
        String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", videoId);
        Uri uriVideo = Uri.parse(mthaiUrl);
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, uriVideo));
    }

    private void openMThaiVideoPassword(final String videoId, final String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("This video has password");
        builder.setMessage("Password : " + password);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                openMThaiVideo(videoId);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadMThaiVideoFromWeb(final String videoId) {
        notifyStart();

		selectedVideoId = videoId;
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

		String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", videoId);
		Request request = new Request.Builder()
				.get()
				.url(mthaiUrl)
				.header("User-Agent", Constant.UserAgentChrome)
				.build();
		okClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(okhttp3.Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					String bodyString = response.body().string();
					playMthaiFromHTML(bodyString);
				}
				notifyFinish();
			}

			@Override
			public void onFailure(okhttp3.Call call, IOException e) {
				notifyFinish();
			}

		});
    }

	private void loadMThaiVideoWithPassword(final String videoId,
			final String password) {
        notifyStart();

		selectedVideoId = videoId;
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

		String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", videoId);
		RequestBody requestBody = new FormBody.Builder()
				.add("clip_password", password)
				.build();
		Request request = new Request.Builder()
				.post(requestBody)
				.url(mthaiUrl)
				.header("User-Agent", Constant.UserAgentChrome)
				.build();
		okClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(okhttp3.Call call, Response response) throws IOException {
				if (response.isSuccessful())
					playMthaiFromHTML(response.body().string());
				notifyFinish();
			}

			@Override
			public void onFailure(okhttp3.Call call, IOException e) {
				notifyFinish();
			}
		});
	}

	private void playMthaiFromHTML(String response) {
		notifyFinish();

		if (mThaiSeperateBySources(response))
			return;

		if (mThaiSeperateByObClip(response))
			return;

		if (mThaiSeperateByDefaultClip(response))
			return;

		if (mThaiParseByTagSource((response)))
			return;

		if (password.length() > 0) {
			openMThaiVideoPassword(selectedVideoId, password);
		} else {
			String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", selectedVideoId);
			Uri uriVideo = Uri.parse(mthaiUrl);
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, uriVideo));
		}
	}

	private boolean mThaiSeperateBySources(String response) {
		String varKey = "sources: ";
		int indexStart = response.indexOf(varKey);
		if (indexStart > 0) {
			indexStart += varKey.length();
			int indexEnd = response.indexOf("]", indexStart) + 1;
			String obClipString = response.substring(indexStart, indexEnd);
			try {
				final JSONArray obClips = new JSONArray(obClipString);
				// Select Quality
				if (obClips.length() > 0){
					JSONObject objClip = obClips.getJSONObject(obClips.length()-1);
					playVideo(objClip.getString("file"));
					return true;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	private boolean mThaiSeperateByObClip(String response) {
		String varKey = "obClip = ";
		int indexStart = response.indexOf(varKey);
		if (indexStart > 0) {
			indexStart += varKey.length();
			int indexEnd = response.indexOf(";", indexStart);
			String obClipString = response.substring(indexStart, indexEnd);
			try {
				JSONArray obClip = new JSONArray(obClipString);
				if (obClip.length() > 0){
					JSONObject objClip = obClip.getJSONObject(0);
					playVideo(objClip.getString("src"));
					return true;
				}
			} catch (JSONException e) {
				return false;
			}
		}
		return false;
	}

	private boolean mThaiSeperateByDefaultClip(String response) {
		String varKey = "defaultClip";
		int indexStart = response.indexOf(varKey);
		if (indexStart > 0) {
			indexStart += varKey.length();
			int indexEnd = response.indexOf(";", indexStart);
			String clipUrl = response.substring(indexStart, indexEnd).replace(" ", "").replace("=", "").replace("\"", "").replace("'", "");
			if (clipUrl.length() > 0) {
				playVideo(clipUrl);
				return true;
			}
		}
		return false;
	}

	private boolean mThaiParseByTagSource(String response) {
		Document doc = Jsoup.parse(response);
		Elements elSource = doc.getElementsByTag("source");
		for (int i = 0; i < elSource.size(); i++) {
			Element eSrc = elSource.get(i);
			String videoUrl = eSrc.attr("src");
			if (videoUrl.length() > 0) {
				playVideo(videoUrl);
				return true;
			}
		}
		return false;
	}
}
