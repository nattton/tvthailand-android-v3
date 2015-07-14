package com.codemobi.android.tvthailand.adapter;

import java.util.HashMap;

import com.codemobi.android.tvthailand.datasource.Episode;
import com.codemobi.android.tvthailand.datasource.Episodes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EpisodeAdapter extends BaseAdapter{
	
	private Activity activity;
	private int resId;
	private Episodes episodes;
	private static LayoutInflater mInflater = null;

	HashMap<String, Integer> videoTypeMap = new HashMap<String, Integer>();
	public EpisodeAdapter(Activity a, Episodes c, int resouceId) {
		videoTypeMap.put("0", com.codemobi.android.tvthailand.R.drawable.ic_youtube);
		videoTypeMap.put("1", com.codemobi.android.tvthailand.R.drawable.ic_dailymotion);
		videoTypeMap.put("11", com.codemobi.android.tvthailand.R.drawable.ic_chrome);
		videoTypeMap.put("12", com.codemobi.android.tvthailand.R.drawable.ic_player);
		videoTypeMap.put("13", com.codemobi.android.tvthailand.R.drawable.ic_player);
		videoTypeMap.put("14", com.codemobi.android.tvthailand.R.drawable.ic_player);
		videoTypeMap.put("15", com.codemobi.android.tvthailand.R.drawable.ic_player);
		
        activity = a;
        episodes = c;
        resId = resouceId;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private static final class ViewHolder {
		public ImageView mediaThumnail;
		public TextView title;
		public TextView aired;
		public TextView viewCount;
		
		public LinearLayout layoutParts;
		public TextView tvParts;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
            holder = new ViewHolder();
            holder.mediaThumnail = (ImageView)convertView.findViewById(com.codemobi.android.tvthailand.R.id.media_thumnail);
            holder.title = (TextView)convertView.findViewById(com.codemobi.android.tvthailand.R.id.tv_label_ep);
            holder.aired = (TextView)convertView.findViewById(com.codemobi.android.tvthailand.R.id.tv_on_air_ep);
            holder.viewCount = (TextView)convertView.findViewById(com.codemobi.android.tvthailand.R.id.view_count_ep);
            holder.layoutParts = (LinearLayout)convertView.findViewById(com.codemobi.android.tvthailand.R.id.part_updated_ll);
            holder.tvParts = (TextView)convertView.findViewById(com.codemobi.android.tvthailand.R.id.num_part);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Episode item = episodes.get(position);
		
		if (videoTypeMap.containsKey(item.getSrcType())) {
			holder.mediaThumnail.setImageResource(videoTypeMap.get(item.getSrcType()));
		} 
		else {
//			holder.mediaThumnail.setImageResource(R.drawable.ic_error);
		}
		
		holder.title.setText(item.getTitle());
		holder.aired.setText(item.getDate());
		holder.viewCount.setText(item.getViewCount());
		if (!item.getParts().equals("")) {
			holder.layoutParts.setVisibility(View.VISIBLE);
			holder.tvParts.setText(item.getParts());
		}
		else {
			holder.layoutParts.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return episodes.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
