package com.makathon.tvthailand.database;

import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.makathon.tvthailand.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.utils.Base64;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class EpisodeModel extends ModelBase {
	protected Context context;
	private int id;
	private String programId;
	private String epId;
	private int ep;
	private String title;
	private String videoEncrypt;
	private String srcType;
	private String date;
	private String viewCount;
	private String parts;
	private String password;
	
	private String[] videos;

	protected EpisodeModel() {
		super();
	}

	public EpisodeModel(Context c, String programId, String epId, int ep,
			String title, String videoEncrypt, String srcType, String date,
			String viewCount, String parts, String password) {
		this.programId = programId;
		this.epId = epId;
		this.ep = ep;
		this.videoEncrypt = videoEncrypt;
		this.srcType = srcType;
		this.date = String.format(c.getString(R.string.aired),
				dateToFormat(date));
		this.viewCount = intToFormat(viewCount);
		this.parts = parts;
		this.password = password;
		this.videoEncrypt = videoEncrypt;
		
		StringBuilder displayBuilder = new StringBuilder();
		if (this.ep > 20000000) {

			if (!title.equals("")) {
				displayBuilder.append(title);
			} else {
				displayBuilder.append(this.date);
			}
		} else {
			displayBuilder.append(String.format(c.getString(R.string.episode),
					this.ep));
			if (!title.equals("")) {
				displayBuilder.append(" - " + title);
			}
		}

		this.title = displayBuilder.toString();
		
	}

	public String getProgramId() {
		return programId;
	}

	public String getEpId() {
		return epId;
	}

	public int getEp() {
		return ep;
	}

	public String getTitle() {
		return title;
	}

	public String getVideoEncrypt() {
		return videoEncrypt;
	}

	public String getSrcType() {
		return srcType;
	}

	public String getDate() {
		return date;
	}

	public String getViewCount() {
		return viewCount;
	}

	public String getParts() {
		return parts;
	}

	public String getPassword() {
		return password;
	}
	
	public String[] getVideos() {
		if(videos == null) {
			String videoIds = decodeVideo(videoEncrypt);
			videos = videoIds.split("[,]");
		}
		return videos;
	}

	public String getVideo(int i) {
		if(videos == null) {
			getVideos();
		}
		return videos[i];
	}
	
	public int size() {
		if(videos == null) {
			getVideos();
		}
		return videos.length;
	}

	private String decodeVideo(String encrypt) {
		String decode = "";
		try {
			decode = encrypt.replace('-', '+').replace('_', '/')
					.replace(',', '=').replace('!', 'a').replace('@', 'b')
					.replace('#', 'c').replace('$', 'd').replace('%', 'e')
					.replace('^', 'f').replace('&', 'g').replace('*', 'h')
					.replace('(', 'i').replace(')', 'j').replace('{', 'k')
					.replace('}', 'l').replace('[', 'm').replace(']', 'n')
					.replace(':', 'o').replace(';', 'p').replace('<', 'q')
					.replace('>', 'r').replace('?', 's');
			byte[] decoded = Base64.decode(decode.getBytes());
			decode = new String(decoded);

		} catch (IOException e) {
			Log.e("VideoId", "DecodeException");
		}
		return decode;
	}

	@SuppressLint("SimpleDateFormat")
	private String dateToFormat(String strDate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyy-MM-dd");
			SimpleDateFormat localeFormatter = new SimpleDateFormat(
					"dd MMMM yyyy", Locale.getDefault());
			return localeFormatter.format(formatter.parse(strDate)).toString();
		} catch (java.text.ParseException e) {
		}
		return "";
	}

	private String intToFormat(String i) {
		return NumberFormat.getInstance().format(Integer.parseInt(i));
	}

	@Override
	public void fromCursor(Cursor cursor, Context context) {
		this.id = cursor.getInt(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns._ID));
		this.programId = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.PROGRAM_ID));
		this.epId = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.EP_ID));
		this.ep = cursor.getInt(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.EP));
		this.title = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.TITLE));
		this.videoEncrypt = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.VIDEO_ENCRYPT));
		this.srcType = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.SRC_TYPE));
		this.date = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.DATE));
		this.viewCount = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.VIEW_COUNT));
		this.parts = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.PARTS));
		this.password = cursor.getString(cursor
				.getColumnIndex(EpisodeTable.EpisodeColumns.PASSWORD));

		this.context = context;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(EpisodeTable.EpisodeColumns.PROGRAM_ID, this.programId);
		values.put(EpisodeTable.EpisodeColumns.EP_ID, this.epId);
		values.put(EpisodeTable.EpisodeColumns.EP, this.ep);
		values.put(EpisodeTable.EpisodeColumns.TITLE, this.title);
		values.put(EpisodeTable.EpisodeColumns.VIDEO_ENCRYPT, this.videoEncrypt);
		values.put(EpisodeTable.EpisodeColumns.SRC_TYPE, this.srcType);
		values.put(EpisodeTable.EpisodeColumns.DATE, this.date);
		values.put(EpisodeTable.EpisodeColumns.VIEW_COUNT, this.viewCount);
		values.put(EpisodeTable.EpisodeColumns.PARTS, this.parts);
		values.put(EpisodeTable.EpisodeColumns.PASSWORD, this.password);
		return values;
	}

	@Override
	public int getId() {
		return this.id;
	}

	public static EpisodeModel newInstance(Cursor cursor, Context context) {
		EpisodeModel episode = new EpisodeModel();
		episode.fromCursor(cursor, context);
		return episode;
	}
	
	public void sendView() { 
		AppUtility.getInstance().viewEpisode(epId, new AsyncHttpResponseHandler());
	}
}
