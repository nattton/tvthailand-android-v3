package com.codemobi.android.tvthailand.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.datasource.Program;
import com.codemobi.android.tvthailand.datasource.Programs;

public class ProgramAdapter extends BaseAdapter{
	private Programs programs;
	private boolean isDisplayImage = true;

	public ProgramAdapter(Programs c) {
        programs = c;
	}

	private static final class ViewHolder {
		public TextView title;
		public TextView description;
		public ImageView thumbnail;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.whatnew_grid_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.title);
            holder.title.setSelected(true);
		    holder.description = (TextView)convertView.findViewById(R.id.description);
		    holder.description.setSelected(true);
		    holder.thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		Program item = programs.get(position);
		holder.title.setText(item.getTitle());
		holder.description.setText(item.getDescription());
		if (isDisplayImage) {
			holder.thumbnail.setVisibility(View.VISIBLE);

			Glide.with(parent.getContext())
					.load(item.getThumbnail())
					.placeholder(R.drawable.ic_tvthailand_show_placeholder)
					.crossFade()
					.fitCenter()
					.into(holder.thumbnail);
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
