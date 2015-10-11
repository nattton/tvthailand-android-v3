package com.codemobi.android.tvthailand.datasource;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.JsonObject;

public class Program implements Parcelable {
	private String id;
	private String title;
	private String thumbnail = "";
	private String poster;
	private String description;
	private String detail;
	private String lastEPName;
	private int viewCount;
	private int voteCount;
	
	private int isOTV;
	private String otvId;
	private String otvLogo;

	public Program(String id, String title, String thumbnail,
			String description, String lastEPName, int isOTV, String otvId, String otvLogo) {
		super();
		this.id = id;
		this.title = title;
		this.thumbnail = thumbnail;
		this.description = description;
		this.lastEPName = lastEPName;

		this.isOTV = isOTV;
		this.otvId = otvId;
		this.otvLogo = otvLogo;
	}
	
	public Program(JsonObject jObj) {
		this.id = jObj.get("id").getAsString();
		this.title = jObj.get("title").getAsString();
		this.thumbnail = jObj.get("thumbnail").getAsString();
		this.poster = jObj.get("poster").getAsString();
		this.description = jObj.get("description").getAsString();
		this.detail = jObj.get("detail").getAsString();
		this.lastEPName = jObj.has("last_epname") ? jObj.get("last_epname").getAsString() : "";
		this.viewCount = jObj.has("view_count") ? jObj.get("vote_count").getAsInt() : 0;
		this.voteCount = jObj.has("vote_count") ? jObj.get("vote_count").getAsInt() : 0;

		this.isOTV = jObj.has("is_otv") ? jObj.get("is_otv").getAsInt() : 0;
		this.otvId = jObj.has("otv_id") ? jObj.get("otv_id").getAsString() : "";
		this.otvLogo = jObj.has("otv_logo") ? jObj.get("otv_logo").getAsString(): "";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getDescription() {
		return description;
	}
	
	public String getDetail() {
		return detail;
	}

	public String getLastEPName() {
		return lastEPName;
	}
	
	public String getPoster() {
		if (poster != null && poster.length() > 0) return poster;
		else return thumbnail;
	}

	public int getViewCount() {
		return viewCount;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public int isOTV() {
        return this.isOTV;
	}

	public String getOtvId() {
		return otvId;
	}

	public String getOtvLogo() {
		return otvLogo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(thumbnail);
		dest.writeString(poster);
		dest.writeString(description);
		dest.writeString(detail);
		dest.writeString(lastEPName);
		dest.writeInt(viewCount);
		
		dest.writeInt(isOTV);
		dest.writeString(otvId);
		dest.writeString(otvLogo);
	}
	
	public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {

		@Override
		public Program createFromParcel(Parcel source) {
			return new Program(source);
		}

		@Override
		public Program[] newArray(int size) {
			return new Program[size];
		}
		
	};
	
	private Program(Parcel source) {
		this.id = source.readString();
		this.title = source.readString();
		this.thumbnail = source.readString();
		this.poster = source.readString();
		this.description = source.readString();
		this.detail = source.readString();
		this.lastEPName = source.readString();
		this.viewCount = source.readInt();
		
		this.isOTV = source.readInt();
		this.otvId = source.readString();
		this.otvLogo = source.readString();
	}

}
