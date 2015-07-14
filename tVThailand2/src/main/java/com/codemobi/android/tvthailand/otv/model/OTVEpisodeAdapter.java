package com.codemobi.android.tvthailand.otv.model;

import com.bumptech.glide.Glide;
import com.codemobi.android.tvthailand.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OTVEpisodeAdapter extends BaseAdapter {

	private Activity activity;
	private int resId;
	private OTVEpisodes episodes;
	private static LayoutInflater mInflater = null;
	private String logo;
	
	public OTVEpisodeAdapter(Activity otvShowDetailActivity,
			OTVEpisodes mOTVEpisodes, int episodeListItem, String logo) {
		this.activity = otvShowDetailActivity;
		this.resId = episodeListItem;
		this.episodes = mOTVEpisodes;
		this.logo = logo;
		
		mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private static final class ViewHolder {
		public ImageView mediaThumbnail;
		public TextView title;
		public TextView aired;
		public TextView viewCount;
		
		public LinearLayout layoutParts;
		public TextView tvParts;
		public LinearLayout view_count_ll;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(resId, parent, false);
            holder = new ViewHolder();
            holder.mediaThumbnail = (ImageView)convertView.findViewById(R.id.media_thumnail);
            holder.title = (TextView)convertView.findViewById(R.id.tv_label_ep);
            holder.aired = (TextView)convertView.findViewById(R.id.tv_on_air_ep);
            holder.viewCount = (TextView)convertView.findViewById(R.id.view_count_ep);
            holder.layoutParts = (LinearLayout)convertView.findViewById(R.id.part_updated_ll);
            holder.tvParts = (TextView)convertView.findViewById(R.id.num_part);
            holder.view_count_ll = (LinearLayout)convertView.findViewById(R.id.view_count_ll);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		OTVEpisode episode = episodes.get(position);
		
		holder.layoutParts.setVisibility(View.GONE);
		holder.view_count_ll.setVisibility(View.INVISIBLE);

		Glide.with(parent.getContext())
				.load(logo)
				.centerCrop()
				.placeholder(R.drawable.ic_otv)
				.crossFade()
				.into(holder.mediaThumbnail);

		holder.title.setText(episode.getNameTh() + "  " + episode.getDate());
		holder.aired.setText("Aired : "+episode.getDate());
		
		holder.viewCount.setText("000");

		
		return convertView;
	}

}
