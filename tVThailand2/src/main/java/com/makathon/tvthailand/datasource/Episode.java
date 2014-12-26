package com.makathon.tvthailand.datasource;

import java.io.IOException;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.makathon.tvthailand.R;
import com.makathon.tvthailand.utils.Base64;
import com.makathon.tvthailand.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.apache.http.Header;

@SuppressWarnings("serial")
public class Episode implements Serializable {
	private String id;
	private int ep;
	private String title;
	private String videoEncrypt;
	private String[] videos;
	private String srcType;
	private String date;
	private String viewCount;
	private String parts;
	private String password;

	public Episode(Context c, String id, int ep, String title,
			String videoEncrypt, String srcType, String date, String viewCount,
			String parts, String password) {
		this.id = id;
		this.ep = ep;
		this.title = title;
		this.videoEncrypt = videoEncrypt;
		this.srcType = srcType;
		this.date = String.format(c.getString(R.string.aired),
				dateToFormat(date));
		this.viewCount = intToFormat(viewCount);
		this.parts = parts;
		this.password = password;
		String videoIds = decodeVideo(this.videoEncrypt);
		videos = videoIds.split("[,]");

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

	public String getId() {
		return id;
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

	public String[] getVideos() {
		return videos;
	}

	public String getSrcType() {
		return srcType;
	}

	public void setSrcType(String srcType) {
		this.srcType = srcType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getViewCount() {
		return viewCount;
	}

	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}

	public String getParts() {
		return parts;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVideo(int position) {
		return videos[position];
	}

	public int size() {
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
//			Log.e("VideoId", "DecodeException");
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
	
	public void sendView() { 
		Utils.getInstance().viewEpisode(id);
	}
}
