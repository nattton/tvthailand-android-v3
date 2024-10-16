package com.codemobi.android.tvthailand.otv.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

public class OTVEpisode implements Parcelable {
	
	private String contentId;
	private String nameTh;
	private String detail;
	private String thumbnail;
	private String cover;
	private String date;
	private ArrayList<OTVPart> parts;
	
	
	public OTVEpisode (String contentId, String nameTh, String detail, String thumbnail, String cover, String date, ArrayList<OTVPart> parts) {
		super();
		this.contentId = contentId;
		this.nameTh = nameTh;
		this.detail = detail;
		this.thumbnail = thumbnail;
		this.cover = cover;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contentId);
        dest.writeString(nameTh);
        dest.writeString(detail);
        dest.writeString(thumbnail);
        dest.writeString(cover);
        dest.writeString(date);
        dest.writeTypedList(parts);
    }

    public static final Parcelable.Creator<OTVEpisode> CREATOR = new Parcelable.Creator<OTVEpisode>() {

        @Override
        public OTVEpisode createFromParcel(Parcel source) {
            return new OTVEpisode(source);
        }

        @Override
        public OTVEpisode[] newArray(int size) {
            return new OTVEpisode[size];
        }
    };

    private OTVEpisode(Parcel source) {
        this.contentId = source.readString();
        this.nameTh = source.readString();
        this.detail = source.readString();
        this.thumbnail = source.readString();
        this.cover = source.readString();
        this.date = source.readString();
        this.parts = new ArrayList<>();
        source.readTypedList(this.parts, OTVPart.CREATOR);
    }
}
