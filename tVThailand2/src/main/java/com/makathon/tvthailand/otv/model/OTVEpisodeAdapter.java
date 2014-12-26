package com.makathon.tvthailand.otv.model;

import com.makathon.tvthailand.R;

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
	
	public OTVEpisodeAdapter(Activity otvShowDetailActivity,
			OTVEpisodes mOTVEpisodes, int episodeListItem) {
		this.activity = otvShowDetailActivity;
		this.resId = episodeListItem;
		this.episodes = mOTVEpisodes;
		
		mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	private static final class ViewHolder {
		public ImageView mediaThumnail;
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
            holder.mediaThumnail = (ImageView)convertView.findViewById(R.id.media_thumnail);
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
		holder.mediaThumnail.setImageResource(R.drawable.ic_otv);
		holder.title.setText(episode.getNameTh() + "  " + episode.getDate());
		holder.aired.setText("Aired : "+episode.getDate());
		
		holder.viewCount.setText("000");

		
		return convertView;
	}

}
