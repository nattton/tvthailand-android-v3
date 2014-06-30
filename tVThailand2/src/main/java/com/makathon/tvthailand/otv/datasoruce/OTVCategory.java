package com.makathon.tvthailand.otv.datasoruce;

import android.os.Parcel;
import android.os.Parcelable;

public class OTVCategory implements Parcelable {
	
	private String id;
	private String apiName;
	private String nameTh;
	private String nameEn;
	private String description;
	
	public OTVCategory(String id, String apiName, String nameTh, String nameEn, String description) {
		super();
		this.id = id;
		this.apiName = apiName;
		this.nameTh = nameTh;
		this.nameEn = nameEn;
		if (description == null || description.equalsIgnoreCase("null")) {
			this.description = "";
		} else {
			this.description = description;
		}
	}

	public String getId() {
		return id;
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getNameTh() {
		return nameTh;
	}

	public String getNameEn() {
		return nameEn;
	}
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description.equalsIgnoreCase("null")) {
			this.description = "";
		} else {
			this.description = description;
		}
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(apiName);
		dest.writeString(nameTh);
		dest.writeString(nameEn);
		dest.writeString(description);
	}
	
	public static final Parcelable.Creator<OTVCategory> CREATOR = new Parcelable.Creator<OTVCategory>() {

		@Override
		public OTVCategory createFromParcel(Parcel source) {
			return new OTVCategory(source);
		}

		@Override
		public OTVCategory[] newArray(int size) {
			return new OTVCategory[size];
		}
		
	};
	
	private OTVCategory(Parcel source) {
		this.id = source.readString();
		this.apiName = source.readString();
		this.nameTh = source.readString();
		this.nameEn = source.readString();
		this.description = source.readString();
	}

}
