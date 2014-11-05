package com.makathon.tvthailand.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makathon.tvthailand.R;

public class Parts {
	private ArrayList<Part> parts = new ArrayList<Part>();
	private String title;
	private String[] videos;
	private String srcType;
	private String password;

	private String display;

	private String selectedVideoId;

    private ProgressDialog progressDialog;

	public interface OnLoadListener {
		void onStart();

		void onFinish();
	}

	public interface OnEpisodeChangeListener {
		void onEpisodeChange(Parts episodes);
	}

	private OnEpisodeChangeListener onEpisodeChangeListener;

	public void setOnProgramChangeListener(
			OnEpisodeChangeListener onEpisodeChangeListener) {
		this.onEpisodeChangeListener = onEpisodeChangeListener;
	}

	private Context mContext;

	public Parts(Context context, String title, String icon, String[] videos,
			String srcType, String password) {
		this.mContext = context;
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

		if (srcType.equals("0")) {
			String videoId = videos[position];
			if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(mContext) == YouTubeInitializationResult.SUCCESS) {
				if(YouTubeIntents.isYouTubeInstalled(mContext)) {
					Intent intent = YouTubeIntents.createPlayVideoIntent(mContext, videoId);
					mContext.startActivity(intent);
				} else {
					mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(
						"http://www.youtube.com/watch?v=%s", videoId))));
				}
				
//				Intent intent = new Intent(mContext,
//						YoutubePlayerViewActivity.class);
//				intent.putExtra(YoutubePlayerViewActivity.EXTRAS_TITLE, title);
//				intent.putExtra(YoutubePlayerViewActivity.EXTRAS_VIDEO_ID,
//						videos[position]);
//				mContext.startActivity(intent);
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
		} else if (srcType.equals("1")) {
			openWithDailyMotion(videos[position]);
		} else if (srcType.equals("11")) {
			Uri uriVideo = Uri.parse(videos[position]);
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, uriVideo));
		} else if (srcType.equals("12")) {
			playVideo(videos[position]);
		} else if (srcType.equals("13")) {
			loadMThaiVideo(videos[position]);
//            openMThaiVideo(videos[position]);
		} else if (srcType.equals("14")) {
			loadMThaiVideoFromWeb(videos[position]);
//            openMThaiVideo(videos[position]);
		} else if (srcType.equals("15")) {
			loadMThaiVideoWithPassword(videos[position], password);
//            openMThaiVideoPassword(videos[position], password);
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

			// Intent LaunchIntent =
			// mContext.getPackageManager().getLaunchIntentForPackage("com.dailymotion.dailymotion");
			// LaunchIntent.putExtra(Intent.ACTION_VIEW, uriVideo);
			// mContext.startActivity(LaunchIntent);
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

		// if (isOpenWith) {
		// isOpenWith = false;
		// startViedo(path);
		// } else {
		// Intent intent = new Intent(mContext, VideoViewActivity.class);
		// intent.putExtra(VideoViewActivity.EXTRAS_TITLE, display);
		// intent.putExtra(VideoViewActivity.EXTRAS_ICON, icon);
		// intent.setData(Uri.parse(path));
		// mContext.startActivity(intent);
		// }
	}

	private int retryCount = 0;

	private void loadMThaiVideo(final String videoKey) {
		retryCount++;
		if (retryCount > 3) {
			retryCount = 0;
			Toast.makeText(mContext, "Can't load video", Toast.LENGTH_LONG)
					.show();
			return;
		}
		String mthaiUrl = String.format(
				"http://video.mthai.com/get_config_event.php?id=%s", videoKey);
		AsyncHttpClient videoClient = new AsyncHttpClient();
		videoClient.get(mthaiUrl, new JsonHttpResponseHandler() {
			public void onSuccess(JSONObject jObj) {
				try {
					JSONArray jPlaylist = jObj.getJSONArray("playlist");
					JSONObject jPlay = jPlaylist.getJSONObject(1);
					String videoUrl = jPlay.getString("url");
					String[] seperateUrl = videoUrl.split("/");
					if (seperateUrl.length == 0)
						return;
					if (seperateUrl[seperateUrl.length - 1]
							.startsWith(videoKey))
						playVideo(videoUrl);
					else
						loadMThaiVideo(videoKey);

				} catch (JSONException e) {
//					Log.e("loadMVideo", "JSONException");
					Toast.makeText(mContext, "Video have problem!!!",
							Toast.LENGTH_LONG).show();
				}
			}

			ProgressDialog dialog;

			public void onStart() {
				dialog = ProgressDialog.show(mContext, "",
						"Loading, Please wait...", true);
			}

			public void onFinish() {
				dialog.dismiss();
			}

			public void onFailure(Throwable error) {
				Toast.makeText(mContext, "Loading fail!!!", Toast.LENGTH_LONG)
						.show();
			}
		});
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

//	private void loadMThaiVideoFromWeb(final String videoKey) {
//		retryCount++;
//		if (retryCount > 3) {
//			retryCount = 0;
//			Toast.makeText(mContext, "Can't load video", Toast.LENGTH_LONG)
//					.show();
//			return;
//		}
//
//		String mthaiUrl = String.format(
//				"http://video.mthai.com/cool/player/%s.html", videoKey);
//		AsyncHttpClient videoClient = new AsyncHttpClient();
//		videoClient.setUserAgent(AppUtility.getUserAgentiOS(mContext));
//		videoClient.get(mthaiUrl, new AsyncHttpResponseHandler() {
//			@Override
//			public void onSuccess(String content) {
//				try {
//					Document doc = Jsoup.parse(content);
//					Elements elSource = doc.getElementsByTag("source");
//					for (int i = 0; i < elSource.size(); i++) {
//						Element eSrc = elSource.get(i);
//						String videoUrl = eSrc.attr("src");
//						String[] seperateUrl = videoUrl.split("/");
//						if (seperateUrl.length == 0)
//							return;
//						if (seperateUrl[seperateUrl.length - 1]
//								.startsWith(videoKey)) {
//							playVideo(videoUrl);
//							return;
//						}
//					}
//
//					loadMThaiVideoFromWeb(videoKey);
//
//				} catch (Exception e) {
////					Log.e("loadMVideFromWeb", "Exception");
//					Toast.makeText(mContext, "Video have problem!!!",
//							Toast.LENGTH_LONG).show();
//				}
//			}
//
//			ProgressDialog dialog;
//
//			public void onStart() {
//				dialog = ProgressDialog.show(mContext, "",
//						"Loading, Please wait...", true);
//			}
//
//			public void onFinish() {
//				dialog.dismiss();
//			}
//
//			public void onFailure(Throwable error) {
//				Toast.makeText(mContext, "Loading fail!!!", Toast.LENGTH_LONG)
//						.show();
//			}
//		});
//	}

    private void loadMThaiVideoFromWeb(final String videoId) {
        this.progressDialog = ProgressDialog.show(mContext, "",
                "Loading, Please wait...", true);
        selectedVideoId = videoId;
        String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", videoId);

        RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
        StringRequest mthaiRequest = new StringRequest(Method.POST, mthaiUrl, createMthaiReqSuccessListener(), createMthaiReqErrorListener()) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", AppUtility.getUserAgentChrome());
                return params;
            }
        };

        mRequestQueue.add(mthaiRequest);
    }

	private void loadMThaiVideoWithPassword(final String videoId,
			final String password) {
        this.progressDialog = ProgressDialog.show(mContext, "",
                "Loading, Please wait...", true);

		selectedVideoId = videoId;
		String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", videoId);
		
		RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
		StringRequest mthaiRequest = new StringRequest(Method.POST, mthaiUrl, createMthaiReqSuccessListener(), createMthaiReqErrorListener()) {
			protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("clip_password", password);
                return params;
            };
            
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            	Map<String, String> params = new HashMap<String, String>();
                params.put("User-Agent", AppUtility.getUserAgentChrome());
            	return params;
            }
		};
		
		mRequestQueue.add(mthaiRequest);
	}
	
	private Response.Listener<String> createMthaiReqSuccessListener() {
		return new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
                progressDialog.dismiss();
				String varKey = "{ mp4:  \"http";
				int indexStart = response.indexOf(varKey) + varKey.length();
				int indexEnd = response.indexOf("}", indexStart);
				String clipUrl = response.substring(indexStart - 4, indexEnd).replace(" ", "").replace("=", "").replace("\"", "").replace("'", "");
				
				String[] seperateUrl = clipUrl.split("/");
				if (seperateUrl[seperateUrl.length - 1]
						.startsWith(selectedVideoId)) {
					playVideo(clipUrl);
					return;
				}
				
				Document doc = Jsoup.parse(response);
				Elements elSource = doc.getElementsByTag("source");
				for (int i = 0; i < elSource.size(); i++) {
					Element eSrc = elSource.get(i);
					String videoUrl = eSrc.attr("src");
					seperateUrl = videoUrl.split("/");
					if (seperateUrl.length == 0)
						return;
					if (seperateUrl[seperateUrl.length - 1]
							.startsWith(selectedVideoId)) {
						playVideo(videoUrl);
						return;
					}
				}

                if (password.length() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("This video has password");
                    builder.setMessage("Password : " + password);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openMThaiVideo(selectedVideoId);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", selectedVideoId);
                    Uri uriVideo = Uri.parse(mthaiUrl);
                    mContext.startActivity(new Intent(Intent.ACTION_VIEW, uriVideo));
                }
			}
		};
	}
	
	private Response.ErrorListener createMthaiReqErrorListener() {
		return new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
				Log.e("response", error.getMessage());
				Toast.makeText(mContext, "Can't play video. please try again.", Toast.LENGTH_LONG).show();
			}
		};
	}


}
