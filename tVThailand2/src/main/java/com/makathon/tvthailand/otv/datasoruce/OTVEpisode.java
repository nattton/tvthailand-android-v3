package com.makathon.tvthailand.otv.datasoruce;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;

public class OTVEpisode {
	
	private String contentId;
	private String nameTh;
	private String nameEn;
	private String detail;
	private String thumbnail;
	private String cover;
	private String ratingStatus;
	private String ratingPoint;
	private String date;
	private ArrayList<OTVPart> parts;
	
	
	public OTVEpisode (String contentId, String nameTh, String nameEn, String detail, String thumbnail, String cover, String ratingStatus, String ratingPoint, String date, ArrayList<OTVPart> parts) {
		super();
		this.contentId = contentId;
		this.nameTh = nameTh;
		this.nameEn = nameEn;
		this.detail = detail;
		this.thumbnail = thumbnail;
		this.cover = cover;
		this.ratingStatus = ratingStatus;
		this.ratingPoint = ratingPoint;
		this.date = String.format(dateToFormat(date));
		this.parts = parts;
	}


	public String getContentId() {
		return contentId;
	}


	public void setContentId(String content_id) {
		this.contentId = content_id;
	}


	public String getNameTh() {
		return nameTh;
	}


	public void setNameTh(String name_th) {
		this.nameTh = name_th;
	}


	public String getNameEn() {
		return nameEn;
	}


	public void setNameEn(String name_en) {
		this.nameEn = name_en;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}


	public String getThumbnail() {
		return thumbnail;
	}


	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}


	public String getCover() {
		return cover;
	}


	public void setCover(String cover) {
		this.cover = cover;
	}


	public String getRatingStatus() {
		return ratingStatus;
	}


	public void setRatingStatus(String ratingStatus) {
		this.ratingStatus = ratingStatus;
	}


	public String getRatingPoint() {
		return ratingPoint;
	}


	public void setRatingPoint(String ratingPoint) {
		this.ratingPoint = ratingPoint;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = String.format(dateToFormat(date));
	}


	public ArrayList<OTVPart> getParts() {
		return parts;
	}


	public void setParts(ArrayList<OTVPart> parts) {
		this.parts = parts;
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


}
