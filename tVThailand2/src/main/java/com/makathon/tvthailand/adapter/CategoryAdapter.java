package com.makathon.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makathon.tvthailand.R;
import com.makathon.tvthailand.datasource.Categories;
import com.makathon.tvthailand.datasource.Category;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryAdapter extends BaseAdapter {
	private ImageLoader imageLoader;

	private Activity activity;
	private int resId;
	private Categories categories;
	private static LayoutInflater mInflater = null;

	public CategoryAdapter(Activity a, Categories c, int resouceId,
			ImageLoader imageLoader) {
		activity = a;
		categories = c;
		resId = resouceId;
		mInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.imageLoader = imageLoader;
	}

	private static final class ViewHolder {
		TextView title;
		NetworkImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
			holder = new ViewHolder();

			holder.title = (TextView) convertView
					.findViewById(R.id.tv_label_cate);
			holder.thumbnail = (NetworkImageView) convertView
					.findViewById(R.id.thumb_cate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Category item = categories.get(position);
		holder.title.setText(item.getTitle());
		holder.thumbnail.setImageUrl(item.getThumbnail(), imageLoader);
		return convertView;
	}

	@Override
	public int getCount() {
		return categories.size();
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
