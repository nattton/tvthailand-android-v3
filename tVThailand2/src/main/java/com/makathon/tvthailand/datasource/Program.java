package com.makathon.tvthailand.datasource;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Program implements Parcelable {
	private String id;
	private String title;
	private String thumbnail = "";
	private String poster;
	private String description;
	private String detail;
	private float rating;
	private String lastEPName;
	private int viewCount;
	private int voteCount;
	
	private int isOTV;
	private String otvId;
	private String otvApiName;

	public Program(String id, String title, String thumbnail,
			String description, float rating, int isOTV, String otvId, String otvApiName) {
		super();
		this.id = id;
		this.title = title;
		this.thumbnail = thumbnail;
		this.description = description;
		this.rating = rating;
		
		this.isOTV = isOTV;
		this.otvId = otvId;
		this.otvApiName = otvApiName;
	}

	public Program(String id, String title, String thumbnail,
			String description, float rating, String lastEPName, int isOTV, String otvId, String otvApiName) {
		super();
		this.id = id;
		this.title = title;
		this.thumbnail = thumbnail;
		this.description = description;
		this.rating = rating;
		this.lastEPName = lastEPName;
		
		this.isOTV = isOTV;
		this.otvId = otvId;
		this.otvApiName = otvApiName;
	}
	
	public Program(JSONObject jObj) {
		try {
			this.id = jObj.getString("id");
			this.title = jObj.getString("title");
			this.thumbnail = jObj.getString("thumbnail");
			this.poster = jObj.getString("poster");
			this.description = jObj.getString("description");
			this.detail = jObj.getString("detail");
			this.rating = jObj.has("rating") ? jObj.getLong("rating"): 0;
			this.lastEPName = jObj.has("last_epname") ? jObj.getString("last_epname"): "";
			this.viewCount = jObj.has("view_count") ? jObj.getInt("vote_count"): 0;
			this.voteCount = jObj.has("vote_count") ? jObj.getInt("vote_count"): 0;
			
			this.isOTV = jObj.has("is_otv") ? jObj.getInt("is_otv"): 0;
			this.otvId = jObj.has("otv_id") ? jObj.getString("otv_id"): "";
			this.otvApiName = jObj.has("otv_api_name") ? jObj.getString("otv_api_name"): "";
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
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

	public float getRating() {
		return rating;
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
//		return isOTV;
//      force no otv
//      return 0;
        return this.isOTV;
	}

	public String getOtvId() {
		return otvId;
	}

	public String getOtvApiName() {
		return otvApiName;
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
		dest.writeFloat(rating);
		dest.writeString(lastEPName);
		dest.writeInt(viewCount);
		
		dest.writeInt(isOTV);
		dest.writeString(otvId);
		dest.writeString(otvApiName);
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
		this.rating = source.readFloat();
		this.lastEPName = source.readString();
		this.viewCount = source.readInt();
		
		this.isOTV = source.readInt();
		this.otvId = source.readString();
		this.otvApiName = source.readString();
	}

}
