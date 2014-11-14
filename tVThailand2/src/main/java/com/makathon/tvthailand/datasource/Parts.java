package com.makathon.tvthailand.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import android.util.Log;

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
import com.makathon.tvthailand.R;

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
		} else if (srcType.equals("13") || srcType.equals("14")) {
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
        notifyStart();

		selectedVideoId = videoId;
		String mthaiUrl = String.format("http://video.mthai.com/cool/player/%s.html", videoId);
		
		RequestQueue mRequestQueue = Volley.newRequestQueue(mContext);
		StringRequest mthaiRequest = new StringRequest(Method.POST, mthaiUrl, createMthaiReqSuccessListener(), createMthaiReqErrorListener()) {
			protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("clip_password", password);
                return params;
            };
            
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
            	Map<String, String> params = new HashMap<>();
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
                notifyFinish();

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
                    openMThaiVideoPassword(selectedVideoId, password);
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
                notifyFinish();
                notifyError("Can't play video. please try again.");
			}
		};
	}


}
