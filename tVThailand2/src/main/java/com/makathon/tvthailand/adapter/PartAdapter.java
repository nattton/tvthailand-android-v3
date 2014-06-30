package com.makathon.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.datasource.Part;
import com.makathon.tvthailand.datasource.Parts;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PartAdapter extends BaseAdapter{
	private Activity activity;
	private int resId;
	private ImageLoader imageLoader;
	private Parts parts;
	private static LayoutInflater mInflater = null;

	public PartAdapter(Activity a, Parts c, int resouceId, ImageLoader mImageLoader) {
        activity = a;
        parts = c;
        resId = resouceId;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = mImageLoader;
	}

	private static final class ViewHolder {
		public NetworkImageView thumbnail;
		public TextView title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.title);
		    holder.thumbnail = (NetworkImageView)convertView.findViewById(R.id.thumbnail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Part item = parts.get(position);
		holder.title.setText(item.getTitle());
		if (item.getThumbnail() != null && item.getThumbnail() != "") {
			holder.thumbnail.setImageUrl(item.getThumbnail(), imageLoader);
		} else {
			holder.thumbnail.setImageResource(R.drawable.ic_tvthailand_120);
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return parts.size();
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
