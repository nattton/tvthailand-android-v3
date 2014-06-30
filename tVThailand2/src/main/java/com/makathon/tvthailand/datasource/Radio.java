package com.makathon.tvthailand.datasource;

public class Radio {
	private String id;
	private String title;
	private String description;
	private String thumbnail;
	private String url;
	private String category;
	private int headerId;




	public Radio(String id, String title, String description,
			String thumbnail, String url, String category, int headerId) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		this.thumbnail = thumbnail;
		this.url = url;
		this.category = category;
		this.headerId = headerId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getHeaderId() {
		return headerId;
	}

	public void setHeaderId(int headerId) {
		this.headerId = headerId;
	}
}
