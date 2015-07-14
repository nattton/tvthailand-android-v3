package com.codemobi.android.tvthailand.otv.model;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.codemobi.android.tvthailand.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OTVShowsAdapter extends BaseAdapter {
	private ImageLoader imageLoader;

	private Activity activity;
	private int resId;
	private OTVShows shows;
	private boolean isDisplayImage = true;
	private static LayoutInflater mInflater = null;

	public OTVShowsAdapter(Activity otvShowListActivity, OTVShows mOTVShows,
			int whatnewGridItem, ImageLoader imageLoader) {
		this.activity = otvShowListActivity;
		this.shows = mOTVShows;
		this.resId = whatnewGridItem;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = imageLoader;
	}

	private static final class ViewHolder {
		public TextView title;
		public TextView description;
		public NetworkImageView thumbnail;
	}

	@Override
	public int getCount() {
		return shows.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.title.setSelected(true);
			holder.description = (TextView) convertView
					.findViewById(R.id.description);
			holder.description.setSelected(true);
			holder.thumbnail = (NetworkImageView) convertView
					.findViewById(R.id.thumbnail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		OTVShow show = shows.get(position);
		holder.title.setText(show.getNameTh());
		holder.description.setText(show.getDetail());
		if (isDisplayImage) {
			holder.thumbnail.setVisibility(View.VISIBLE);
			holder.thumbnail.setImageUrl(show.getThumbnail(), imageLoader);
		} else {
			holder.thumbnail.setVisibility(View.GONE);
		}

		return convertView;
	}
}
