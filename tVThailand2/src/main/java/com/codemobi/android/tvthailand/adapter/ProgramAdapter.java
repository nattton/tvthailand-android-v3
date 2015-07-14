package com.codemobi.android.tvthailand.adapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProgramAdapter extends BaseAdapter{
	private ImageLoader imageLoader;
	
	private Activity activity;
	private int resId;
	private Programs programs;
	private boolean isDisplayImage = true;
	private static LayoutInflater mInflater = null;

	public ProgramAdapter(Activity a, Programs c, int resouceId, ImageLoader mImageLoader) {
        activity = a;
        programs = c;
        resId = resouceId;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = mImageLoader;
	}

	private static final class ViewHolder {
		public TextView title;
		public TextView description;
		public NetworkImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.title.setSelected(true);
		    holder.description = (TextView)convertView.findViewById(R.id.description);
		    holder.description.setSelected(true);
		    holder.thumbnail = (NetworkImageView)convertView.findViewById(R.id.thumbnail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Program item = programs.get(position);
		holder.title.setText(item.getTitle());
		holder.description.setText(item.getDescription());
		if (isDisplayImage) {
			holder.thumbnail.setVisibility(View.VISIBLE);
			if (item.getThumbnail() != null) {
				holder.thumbnail.setImageUrl(item.getThumbnail(), imageLoader);
			} else {
				holder.thumbnail.setImageResource(R.drawable.ic_tvthailand_120);
			}
		} else {
			holder.thumbnail.setVisibility(View.GONE);
		}
		
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
