package com.makathon.tvthailand.datasource;

public class Part {
	private int no;
	private String title;
	private String videoKey;
	private String thumbnail;
	private String srcType;
	
	public Part(int no, String videoKey, String srcType) {
		super();
		this.no = no;
		this.title = String.format("Part %s", no);
		this.videoKey = videoKey;
		this.thumbnail = videoThumbnail(srcType, videoKey);
		this.srcType = srcType;
	}
	
	
	
	public int getNo() {
		return no;
	}



	public String getTitle() {
		return title;
	}



	public String getVideoKey() {
		return videoKey;
	}



	public String getThumbnail() {
		return thumbnail;
	}



	public String getSrcType() {
		return srcType;
	}



	public String videoThumbnail(String src_type, String key) {
		if (src_type.equals("0")) {
			return String.format("http://i.ytimg.com/vi/%s/default.jpg", key);
//			return "http://i.ytimg.com/vi/" + key + "/default.jpg";
		} else if (src_type.equals("1")) {
			return String.format("http://www.dailymotion.com/thumbnail/video/%s", key);
//			return "http://www.dailymotion.com/thumbnail/video/" + key;
//			return "http://www.dailymotion.com/thumbnail/160x120/video/" + key;
		}else if (src_type.equals("13") || src_type.equals("14") || src_type.equals("15")) {
			return String.format("http://video.mthai.com/thumbnail/%s.jpg", key);
//			return "http://video.mthai.com/thumbnail/" + key + ".jpg";
		} else {
			return "http://www.makathon.com/placeholder.png";
		}
	}
	

}
