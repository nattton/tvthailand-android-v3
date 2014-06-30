package com.makathon.tvthailand.adapter;

import com.makathon.tvthailand.R;
import com.makathon.tvthailand.datasource.Channel;
import com.makathon.tvthailand.datasource.Channels;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChannelAdapter extends BaseAdapter{
	private Activity activity;
	private int resId;
	private Channels channels;
	private static LayoutInflater mInflater = null;
	private ImageLoader imageLoader;
	
	public ChannelAdapter(Activity a, Channels c, int resouceId, ImageLoader mImageLoader) {
        activity = a;
        channels = c;
        resId = resouceId;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = mImageLoader;
	}

	private static final class ViewHolder {
		TextView channel_tv;
		NetworkImageView channel_icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
            holder = new ViewHolder();
			holder.channel_tv = (TextView) convertView.findViewById(R.id.tv_channel);
			holder.channel_icon = (NetworkImageView) convertView.findViewById(R.id.imv_channel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Channel item = channels.get(position);
		holder.channel_tv.setText(item.getTitle());
		if (item.getThumbnail() != null) {
			holder.channel_icon.setImageUrl(item.getThumbnail(), imageLoader);
		} else {
			holder.channel_icon.setImageResource(R.drawable.ic_tvthailand_120);
		}
		
		return convertView;
	}

	@Override
	public int getCount() {
		return channels.size();
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
