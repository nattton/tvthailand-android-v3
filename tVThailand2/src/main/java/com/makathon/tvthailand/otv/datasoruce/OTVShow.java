package com.makathon.tvthailand.otv.datasoruce;

import android.os.Parcel;
import android.os.Parcelable;

public class OTVShow implements Parcelable {
	
	private String id;
	private String nameTh;
	private String nameEn;
	private String detail;
//	private String director;
//	private String writer;
//	private String actor;
//	private String genre;
	private String release;
	private String rate;
	private String rating;
	private String thumbnail;
	private String cover;
	

	public OTVShow(String id, String nameTh, String nameEn, String detail, String director, String writer, String actor, String genre, String release, String rate, String rating, String thumbnail, String cover){
		super();
		this.id = id;
		this.nameTh = nameTh;
		this.nameEn = nameEn;
		this.detail = detail;
//		this.director = director;
//		this.writer = writer;
//		this.actor = actor;
//		this.genre = genre;
		this.release = release;
		this.rate = rate;
		this.rating = rating;
		this.thumbnail = thumbnail;
		this.cover = cover;
	}
	

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getNameTh() {
		return nameTh;
	}


	public void setNameTh(String nameTh) {
		this.nameTh = nameTh;
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


//	public String getDirector() {
//		return director;
//	}
//
//
//	public void setDirector(String director) {
//		this.director = director;
//	}
//
//
//	public String getWriter() {
//		return writer;
//	}
//
//
//	public void setWriter(String writer) {
//		this.writer = writer;
//	}
//
//
//	public String getActor() {
//		return actor;
//	}
//
//
//	public void setActor(String actor) {
//		this.actor = actor;
//	}
//
//
//	public String getGenre() {
//		return genre;
//	}
//
//
//	public void setGenre(String genre) {
//		this.genre = genre;
//	}


	public String getRelease() {
		return release;
	}


	public void setRelease(String release) {
		this.release = release;
	}


	public String getRate() {
		return rate;
	}


	public void setRate(String rate) {
		this.rate = rate;
	}


	public String getRating() {
		return rating;
	}


	public void setRating(String rating) {
		this.rating = rating;
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
	

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(nameTh);
		dest.writeString(nameEn);
		dest.writeString(detail);
//		dest.writeString(director);
//		dest.writeString(writer);
//		dest.writeString(actor);
//		dest.writeString(genre);
		dest.writeString(release);
		dest.writeString(rate);
		dest.writeString(rating);
		dest.writeString(thumbnail);
		dest.writeString(cover);
	}
	
	public static final Parcelable.Creator<OTVShow> CREATOR = new Parcelable.Creator<OTVShow>() {

		@Override
		public OTVShow createFromParcel(Parcel source) {
			return new OTVShow(source);
		}

		@Override
		public OTVShow[] newArray(int size) {
			return new OTVShow[size];
		}
		
	};
	
	private OTVShow(Parcel source) {
		this.id = source.readString();
		this.nameTh = source.readString();
		this.nameEn = source.readString();
		this.detail = source.readString();
//		this.director = source.readString();
//		this.writer = source.readString();
//		this.actor = source.readString();
//		this.genre = source.readString();
		this.release = source.readString();
		this.rate = source.readString();
		this.rating = source.readString();
		this.thumbnail = source.readString();
		this.cover = source.readString();
	}
	
}
