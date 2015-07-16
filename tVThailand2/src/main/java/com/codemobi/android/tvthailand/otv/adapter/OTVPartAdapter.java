package com.codemobi.android.tvthailand.otv.adapter;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;
import com.codemobi.android.tvthailand.otv.model.OTVPart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OTVPartAdapter extends BaseAdapter {

	private ArrayList<OTVPart> parts;

	public OTVPartAdapter(ArrayList<OTVPart> parts) {
		this.parts = parts;
	}

	private static final class ViewHolder {
		public ImageView thumbnail;
		public TextView title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) parent.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.part_list_item, parent, false);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.thumbnail = (ImageView) convertView
					.findViewById(R.id.thumbnail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		OTVPart item = parts.get(position);

		holder.title.setText(item.getNameTh());
		Glide.with(parent.getContext())
				.load(item.getThumbnail())
				.placeholder(R.drawable.ic_tvthailand_show_placeholder)
				.crossFade()
				.fitCenter()
				.into(holder.thumbnail);
		
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
