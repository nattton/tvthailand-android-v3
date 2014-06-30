package com.makathon.tvthailand.otv.datasoruce;

import java.util.ArrayList;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.datasource.AppUtility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OTVPartAdapter extends BaseAdapter {
	private ImageLoader imageLoader;

	private Activity activity;
	private int resId;
	private ArrayList<OTVPart> parts;
	private static LayoutInflater mInflater = null;

	public OTVPartAdapter(Activity a, ArrayList<OTVPart> parts, int resouceId, ImageLoader mImageLoader) {
		this.activity = a;
		this.parts = parts;
		this.resId = resouceId;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.thumbnail = (NetworkImageView) convertView
					.findViewById(R.id.thumbnail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		OTVPart item = parts.get(position);

		holder.title.setText(item.getNameTh());
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
