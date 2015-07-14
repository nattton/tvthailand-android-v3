package com.codemobi.android.tvthailand.ads;

public class AdRotate {
	private String name;
	private String url;
	private int time;
	private int interval;
	public AdRotate(String name, String url, int time, int interval) {
		this.name = name;
		this.url = url;
		this.time = time;
		this.interval = interval;
	}
	public String getName() {
		return name;
	}
	public String getUrl() {
		return url;
	}
	public int getTime() {
		return time;
	}
	
	public int getInterval() {
		return interval;
	}
}
