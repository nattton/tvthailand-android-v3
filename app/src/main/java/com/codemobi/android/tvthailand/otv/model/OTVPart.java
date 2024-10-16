package com.codemobi.android.tvthailand.otv.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OTVPart implements Parcelable {

	private String partId;
	private String nameTh;
	private String nameEn;
	private String thumbnail;
	private String cover;
	private String streamUrl;
	private String vastUrl;
	private String mediaCode;

    private static final String EMPTY_STRING = "";
	
	public OTVPart() {
		super();
	}

	private String CheckNullString(String str) {
		if (str == null || str.equals("null"))
			return EMPTY_STRING;
		return str;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = CheckNullString(partId);
	}

	public String getNameTh() {
		return nameTh;
	}

	public void setNameTh(String nameTh) {
		this.nameTh = CheckNullString(nameTh);
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = CheckNullString(nameEn);
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = CheckNullString(thumbnail);
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = CheckNullString(cover);
	}

	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStream_url(String streamUrl) {
		this.streamUrl = CheckNullString(streamUrl);
	}

	public String getVastUrl() {
		return vastUrl;
	}

	public void setVastUrl(String vastUrl) {
		this.vastUrl = CheckNullString(vastUrl);
	}

	public String getMediaCode() {
		return mediaCode;
	}

	public void setMediaCode(String mediaCode) {
		this.mediaCode = CheckNullString(mediaCode);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(partId);
		dest.writeString(nameTh);
		dest.writeString(nameEn);
		dest.writeString(thumbnail);
		dest.writeString(cover);
		dest.writeString(streamUrl);
		dest.writeString(vastUrl);
		dest.writeString(getMediaCode());
	}
	
	public static final Parcelable.Creator<OTVPart> CREATOR = new Parcelable.Creator<OTVPart>() {

		@Override
		public OTVPart createFromParcel(Parcel source) {
			return new OTVPart(source);
		}

		@Override
		public OTVPart[] newArray(int size) {
			return new OTVPart[size];
		}
		
	};
	
	private OTVPart(Parcel source) {
		this.partId = source.readString();
		this.nameTh = source.readString();
		this.nameEn = source.readString();
		this.thumbnail = source.readString();
		this.cover = source.readString();
		this.streamUrl = source.readString();
		this.vastUrl = source.readString();
		this.setMediaCode(source.readString());
	}


	
}
