package com.makathon.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.datasource.AppUtility;
import com.makathon.tvthailand.datasource.Program;
import com.makathon.tvthailand.datasource.Programs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopHitsAdapter extends BaseAdapter {
	private ImageLoader imageLoader;

	private Activity activity;
	private int resId;
	private Programs programs;
	private static LayoutInflater mInflater = null;

	public TopHitsAdapter(Activity a, Programs c, int resouceId, ImageLoader imageLoader) {
		activity = a;
		programs = c;
		resId = resouceId;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = imageLoader;
	}

	private static final class ViewHolder {
		public TextView label;
		public TextView description;
		public NetworkImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
			holder = new ViewHolder();
			holder.label = (TextView) convertView.findViewById(R.id.title);
			holder.description = (TextView) convertView
					.findViewById(R.id.description);
			holder.thumbnail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Program item = programs.get(position);
		holder.label.setText(String.valueOf(position + 1) + ". " + item.getTitle());
		holder.description.setText(item.getDescription());
		holder.thumbnail.setVisibility(View.VISIBLE);
		holder.thumbnail.setImageUrl(item.getThumbnail(), imageLoader);
		return convertView;
	}

	@Override
	public int getCount() {
		return programs.size();
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
